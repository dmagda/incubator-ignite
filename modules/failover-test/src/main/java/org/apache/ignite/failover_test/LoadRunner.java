package org.apache.ignite.failover_test;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.internal.util.typedef.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.lang.*;
import org.apache.ignite.transactions.*;

import javax.cache.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 *
 */
public class LoadRunner {
    public static final String ITEMS_CACHE_NAME = "tap_items";
    public static final String CHILDREN_CACHE_NAME = "tap_children";

    public static final int CONFERENCES = 2_000;
    public static final int CALLERS = 20_000;

    /** */
    private Ignite ignite;

    /** */
    private TapItemServiceImpl service;

    /** */
    private final int poolSize = Runtime.getRuntime().availableProcessors() * 2;

    /** */
    private ExecutorService execSvc = Executors.newFixedThreadPool(poolSize);

    /** Available callers. */
    private ConcurrentLinkedDeque<TapItem> availableCallers = new ConcurrentLinkedDeque<>();

    /** Busy callers. */
    private ConcurrentLinkedDeque<TapItem> busyCallers = new ConcurrentLinkedDeque<>();

    /** Conferences. */
    private ConcurrentMap<String, TapItem> conferences = new ConcurrentHashMap<>();

    /**
     * @param ignite Ignite.
     */
    public LoadRunner(Ignite ignite) {
        this.ignite = ignite;

        service = new TapItemServiceImpl(ignite, ignite.<String, TapItem>cache(ITEMS_CACHE_NAME), ignite.<String, Set<String>>cache(CHILDREN_CACHE_NAME));
    }

    /**
     *
     */
    public void loadInitialData() throws Exception {
        try (IgniteDataStreamer streamer = ignite.dataStreamer(ITEMS_CACHE_NAME)) {
            for (int i = 0; i < CONFERENCES; i++) {
                HashMap attrs = new HashMap();

                attrs.put("ATTR1", 0);
                attrs.put("ATTR2", 0);

                TapItem item = new TapItem("CONF-" + i, attrs);

                conferences.put(item.getId(), item);

                streamer.addData(item.getId(), item);
            }
        }

        ignite.log().warning("Done loading conferences.");

        try (IgniteDataStreamer streamer = ignite.dataStreamer(ITEMS_CACHE_NAME)) {
            for (int i = 0; i < CALLERS; i++) {
                HashMap attrs = new HashMap();

                attrs.put("ATTR1", 0);
                attrs.put("ATTR2", 0);

                TapItem item = new TapItem("CALLER-" + i, attrs);

                availableCallers.offer(item);

                streamer.addData(item.getId(), item);
            }
        }

        ignite.log().warning("Done loading callers.");
    }

    /**
     *
     */
    public void run() throws Exception {
        Collection<Future> futs = new ArrayList<>(poolSize);
        final AtomicLong cnt = new AtomicLong();

        for (int i = 0; i < poolSize; i++) {
            futs.add(execSvc.submit(new Callable() {
                @Override public Object call() throws Exception {
                    try {
                        runTest(cnt);
                    }
                    catch (Throwable e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }));
        }

        long lastTimeNonZeroVal = System.currentTimeMillis();
        boolean printStack = false;

        while (!Thread.currentThread().isInterrupted()) {
            Thread.sleep(1_000);

            long val = cnt.getAndSet(0);

            if (val != 0)
                lastTimeNonZeroVal = System.currentTimeMillis();

            ignite.log().warning("TPS: " + val);

            if (!printStack && System.currentTimeMillis() - lastTimeNonZeroVal >= 2 * 60 * 1000) {
                U.dumpStack("CONSIDER HANGNING");
                printStack = true;
            }
        }
    }

    private void runTest(AtomicLong cnt) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                switch (rnd.nextInt(4)) {
                    case 0:
                        addRandomCaller(rnd);

                        break;

                    case 1:
                        dropRandomCaller(rnd);

                        break;

                    case 2:
                        updateRandomConf(rnd);

                        break;

                    case 3:
                        updateRandomCaller(rnd);

                        break;
                }

                cnt.incrementAndGet();
            }
        }
        finally {
            System.err.println("THREAD IS STOPPED");
        }
    }

    private void updateRandomCaller(ThreadLocalRandom rnd) {
        TapItem item = randomCaller(rnd, busyCallers);

        if (item == null)
            return;

        try {
            HashMap attrs = new HashMap();

            attrs.put("ATTR1", rnd.nextInt());

            while (true) {
                try {
                    service.updateItem(item.getId(), attrs, null);

                    break;
                }
                catch (Exception e) {
                    if (!retryOnException(e)) {
                        System.err.println("OPERATION FAILED: " + e.getMessage());

                        throw new RuntimeException("OPERATION FAILED", e);
                    }
                }
            }
        }
        finally {
            busyCallers.offer(item);
        }
    }

    private void addRandomCaller(ThreadLocalRandom rnd) {
        TapItem item = randomCaller(rnd, availableCallers);

        if (item == null)
            return;

        addRandomCaller(rnd, item);
    }

    private void addRandomCaller(ThreadLocalRandom rnd, TapItem item) {
        String confId = "CONF-" + rnd.nextInt(CONFERENCES);

        while (true) {
            try {
                service.push(confId, item.getId(), null);

                break;
            }
            catch (Exception e) {
                if (!retryOnException(e)) {
                    System.err.println("OPERATION FAILED: " + e.getMessage());

                    throw new RuntimeException("OPERATION FAILED", e);
                }
            }
        }

        busyCallers.offer(item);
    }

    private void dropRandomCaller(ThreadLocalRandom rnd) {
        TapItem item = randomCaller(rnd, busyCallers);

        if (item == null)
            return;

        TapItem item0 = null;

        while (true) {
            try {
                item0 = service.getItem(item.getId());

                if (item0 == null || item0.getParentId() == null) {
                    //ignite.log().error("MISSING CALLER: " + item.getId() + ", cache_item=" + item0);

                    addRandomCaller(rnd, item);

                    return;
                }
                else
                    break;
            }
            catch (Exception e) {
                if (!retryOnException(e)) {
                    System.err.println("OPERATION FAILED: " + e.getMessage());

                    throw e;
                }
            }
        }

        while (true) {
            try {
                service.pop(item0.getParentId(), item0.getId(), null);

                break;
            }
            catch (Exception e) {
                if (!retryOnException(e)) {
                    System.err.println("OPERATION FAILED: " + e.getMessage());

                    throw new RuntimeException("OPERATION FAILED", e);
                }
            }
        }

        availableCallers.offer(item);
    }

    private void updateRandomConf(ThreadLocalRandom rnd) {
        TapItem item = randomConference(rnd);

        try {
            HashMap attrs = new HashMap();

            attrs.put("ATTR1", rnd.nextInt());

            while (true) {
                try {
                    service.updateItem(item.getId(), attrs, null);

                    break;
                }
                catch (Exception e) {
                    if (!retryOnException(e)) {
                        System.err.println("OPERATION FAILED: " + e.getMessage());

                        throw new RuntimeException("OPERATION FAILED", e);
                    }
                }
            }
        }
        finally {
            conferences.put(item.getId(), item);
        }
    }

    private TapItem randomCaller(ThreadLocalRandom rnd, Deque<TapItem> items) {
        int shuffle = rnd.nextInt(10);

        for (int i = 0; i < shuffle; i++) {
            TapItem item = items.pollFirst();

            if (item == null)
                return null;

            items.offerLast(item);
        }

        return items.pollFirst();
    }

    private TapItem randomConference(ThreadLocalRandom rnd) {
        while (true) {
            String confId = "CONF-" + rnd.nextInt(CONFERENCES);

            TapItem item = conferences.remove(confId);

            if (item != null)
                return item;
        }
    }

    private boolean retryOnException(Exception ex) {
        try {
            throw ex;
        }
        catch (TransactionRollbackException ignore) {
            // Safe to retry right away.
        }
        catch (CacheServerNotFoundException ignore) {
            // retry
        }
        catch (ClusterTopologyException e) {
            try {
                IgniteFuture<?> fut = e.retryReadyFuture();

                fut.get();
            }
            catch (Exception e2) {
                e2.printStackTrace();
                // retry
            }
            //retry
        }
        catch (IgniteClientDisconnectedException e) {
            // wait for reconnect and retry
            e.reconnectFuture().get();
        }
        catch (CacheException e) {
            if (e.getCause() instanceof ClusterTopologyException) {
                ClusterTopologyException topEx = (ClusterTopologyException)e.getCause();

                try {
                    topEx.retryReadyFuture().get();
                }
                catch (Exception e2) {
                    // retry

                    e2.printStackTrace();
                }

                // retry
            }
            else if (X.hasCause(e, TransactionRollbackException.class)) {
                // Safe to retry right away.
            }
            else if (X.hasCause(e, IgniteCheckedException.class)) {
                // print stack trace and retry

                e.printStackTrace();
            }
            else if (X.hasCause(e, ClusterTopologyException.class)) {
                // retry
            }
            else if (e.getCause() instanceof IgniteClientDisconnectedException) {
                // wait for reconnect and retry
                IgniteClientDisconnectedException cause = (IgniteClientDisconnectedException)e.getCause();

                cause.reconnectFuture().get();
            }
            else {
                e.printStackTrace();

                return false;
            }

        }
        catch (IllegalStateException ignore) {
            // Retry.

            ignore.printStackTrace();
        }
        catch (TapItemNotExistException e) {
            e.printStackTrace();

            return false;
        }
        catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }
}
