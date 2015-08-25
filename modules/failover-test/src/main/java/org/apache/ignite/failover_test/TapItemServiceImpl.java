package org.apache.ignite.failover_test;

import org.apache.ignite.*;
import org.apache.ignite.cache.CacheEntryProcessor;
import org.apache.ignite.cache.query.*;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;

import javax.cache.*;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author Alexey Ivanov
 */
public class TapItemServiceImpl {
    private IgniteLogger LOG;

    private final Ignite ignite;
    private final IgniteCache<String, TapItem> tapCache;
    private final IgniteCache<String, Set<String>> childrenCache;

    public TapItemServiceImpl(Ignite ignite, IgniteCache<String, TapItem> tapCache, IgniteCache<String, Set<String>> childrenCache) {
        this.ignite = ignite;
        this.tapCache = tapCache;
        this.childrenCache = childrenCache;

        LOG = ignite.log();
    }

    private static TapItemNotExistException rctItemNotExistException(String itemId) {
        return new TapItemNotExistException(itemId);
    }

    public TapItem createNewItem(String id, Map<String, Object> attributes, String requestId) {
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }

        TapItem tapItem = new TapItem(id, attributes);

        tapCache.put(id, tapItem);
        notifyNew(id, attributes, requestId);
        return tapItem;
    }

    private void notifyNew(String id, Map<String, Object> attributes, String requestId) {
    }

    private void notifyUpdate(String itemId, Map<String, Object> attributes, String requestId) {
    }

    private void notifyRemove(String itemId, String requestId) {
    }

    private void notifyAddChild(String parentId, String childId, String requestId) {
    }

    private void notifyRemoveChild(String parentId, String childId, String requestId) {
    }

    private void notifyTxStart() {
    }

    private void notifyTxCommit() {
    }

    public TapItem removeItem(final String itemId, final String requestId) throws TapItemNotExistException {
        try {
            return doInTransaction(new Callable<TapItem>() {
                @Override
                public TapItem call() throws Exception {
                    TapItem old = tapCache.getAndRemove(itemId);
                    if (old == null) {
                        throw rctItemNotExistException(itemId);
                    }

                    removeLinkedItems(itemId, old, requestId);
                    notifyRemove(itemId, requestId);

                    return old;
                }
            });
        } catch (RuntimeException | TapItemNotExistException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void removeLinkedItems(final String itemId, final TapItem old, final String requestId) throws Exception {
        doInTransaction(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Set<String> subItemIds = childrenCache.getAndRemove(itemId);
                if (subItemIds != null && !subItemIds.isEmpty()) {
                    Map<String, EntryProcessorResult<String>> resultMap = tapCache.invokeAll(subItemIds, new RemoveParentClosure(itemId));
                    for (Map.Entry<String, EntryProcessorResult<String>> entry : resultMap.entrySet()) {
                        String childId = entry.getKey();
                        EntryProcessorResult<String> processorResult = entry.getValue();
                        String actualParentId = processorResult.get();
                        if (itemId.equals(actualParentId)) {
                            notifyRemoveChild(itemId, childId, requestId);
                        }
                    }
                }

                String parentId = old.getParentId();
                if (parentId != null) {
                    removeFromParent(itemId, parentId, requestId);
                }

                return null;
            }
        });
    }

    private Collection<String> removeFromParent(String itemId, String parentId, String requestId) {
        Collection<String> children = childrenCache.invoke(parentId, new RemoveChildClosure(itemId));
        if (children != null) {
            notifyRemoveChild(parentId, itemId, requestId);
            return children;
        } else {
            if (LOG.isInfoEnabled())
                LOG.info("Couldn't remove child " + itemId + " because tem " + parentId + " has no children.");
        }
        return Collections.emptySet();
    }

    public TapItem getItem(String itemId) {
        return tapCache.get(itemId);
    }

    public TapItem updateItem(String itemId, final Map<String, Object> attributes, String requestId) throws TapItemNotExistException {
        if (itemId == null || itemId.isEmpty()) {
            return createNewItem(itemId, attributes, requestId);
        }

        TapItem tapItem = tapCache.invoke(itemId, new SetPropertiesClosure(itemId, attributes));
        notifyUpdate(itemId, attributes, requestId);
        return tapItem;
    }

    private TapItem getItemSafe(String itemId) {
        TapItem item = tapCache.get(itemId);
        if (item == null) {
            item = new TapItem(itemId, Collections.<String, Object>emptyMap());
        }
        return item;
    }

    private <T> T doInTransaction(Callable<T> action) throws Exception {
        IgniteTransactions transactions = ignite.transactions();
        Transaction existingTx = transactions.tx();
        if (existingTx == null) {
            try (Transaction tx = transactions.txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.REPEATABLE_READ)) {
                notifyTxStart();
                T result = action.call();
                tx.commit();
                notifyTxCommit();
                return result;
            }
        } else {
            return action.call();
        }
    }

    public Number itemAttributeAddAndGet(final String itemId, final String attributeName, final int delta, final String requestId)
        throws TapItemNotExistException {
        try {
            return doInTransaction(new Callable<Number>() {
                @Override
                public Number call() throws Exception {
                    TapItem item = getItemSafe(itemId);
                    Number result = doAttributeAddAndGet(itemId, attributeName, delta, item);
                    tapCache.put(item.getId(), item);
                    notifyUpdate(itemId, Collections.<String, Object>singletonMap(attributeName, result), requestId);
                    return result;
                }
            });
        } catch (RuntimeException | TapItemNotExistException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Number doAttributeAddAndGet(String itemId, String attributeName, int delta, TapItem item) {
        Object attribute = item.getAttribute(attributeName);

        Number numberAttribute = (Number) attribute;
        Number result;
        if (attribute == null) {
            result = delta;
        } else if (attribute instanceof Double) {
            result = numberAttribute.doubleValue() + delta;
        } else if (attribute instanceof Float) {
            result = numberAttribute.floatValue() + delta;
        } else if (attribute instanceof Long) {
            result = numberAttribute.longValue() + delta;
        } else {
            result = numberAttribute.intValue() + delta;
        }
        item.setAttribute(attributeName, result);
        return result;
    }

    public Set<String> getChildren(String itemId) throws TapItemNotExistException {
        checkExists(itemId);
        return doGetChildren(itemId);
    }

    private Set<String> doGetChildren(String itemId) {
        Set<String> children = childrenCache.get(itemId);
        if (children == null) {
            return Collections.emptySet();
        }
        return children;
    }

    public Collection<String> push(final String parentItemId, final String childItemId, final String requestId)
        throws TapItemNotExistException {
        try {
            return doInTransaction(new Callable<Collection<String>>() {
                @Override
                public Collection<String> call() throws TapItemNotExistException {
                    try {
                        checkExists(parentItemId);
                    }
                    catch (TapItemNotExistException e) {
                        // primary and backup nodes could leave topology (both test servers are restarted)
                        HashMap attrs = new HashMap();

                        attrs.put("ATTR1", 0);
                        attrs.put("ATTR2", 0);

                        updateItem(parentItemId, attrs, null);
                    }

                    try {
                        checkExists(childItemId);
                    }
                    catch (TapItemNotExistException e) {
                        // primary and backup nodes could leave topology (both test servers are restarted)
                        HashMap attrs = new HashMap();

                        attrs.put("ATTR1", 0);
                        attrs.put("ATTR2", 0);

                        updateItem(childItemId, attrs, null);
                    }

                    String old = tapCache.invoke(childItemId, new SetParentClosure(parentItemId));
                    if (old != null) {
                        if (old.equals(parentItemId))
                            return doGetChildren(parentItemId);
                    }

                    Collection<String> children = childrenCache.invoke(parentItemId, new AddChildClosure(childItemId));

                    notifyAddChild(parentItemId, childItemId, requestId);

                    return children;
                }
            });
        } catch (RuntimeException | TapItemNotExistException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Collection<String> pop(final String parentItemId, final String childItemId,
                                  final String requestId) throws TapItemNotExistException {
        try {
            return doInTransaction(new Callable<Collection<String>>() {
                @Override
                public Collection<String> call() throws TapItemNotExistException {
                    try {
                        checkExists(parentItemId);
                    }
                    catch (TapItemNotExistException e) {
                        // primary and backup nodes could leave topology (both test servers are restarted)
                        HashMap attrs = new HashMap();

                        attrs.put("ATTR1", 0);
                        attrs.put("ATTR2", 0);

                        updateItem(parentItemId, attrs, null);
                    }

                    try {
                        checkExists(childItemId);
                    }
                    catch (TapItemNotExistException e) {
                        // primary and backup nodes could leave topology (both test servers are restarted)
                        HashMap attrs = new HashMap();

                        attrs.put("ATTR1", 0);
                        attrs.put("ATTR2", 0);

                        updateItem(childItemId, attrs, null);
                    }

                    // Set parent to null.
                    String actualParentId = tapCache.invoke(childItemId, new RemoveParentClosure(parentItemId));

                    if (actualParentId == null)
                        return doGetChildren(parentItemId);

                    return removeFromParent(childItemId, parentItemId, requestId);
                }
            });
        } catch (RuntimeException | TapItemNotExistException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void clear() {
        tapCache.clear();
        childrenCache.clear();
    }

    private Collection<TapItem> getQueryResult(QueryCursor<Cache.Entry<String, TapItem>> query) {
        Collection<TapItem> result = new ArrayList<>();
        for (Cache.Entry<String, TapItem> entry : query) {
            TapItem entryValue = entry.getValue();
            result.add(entryValue);
        }
        return result;
    }

    private Collection<TapItem> getAll(Set<String> items) {
        Map<String, TapItem> all = tapCache.getAllOutTx(items);
        return all.values();
    }

    private void checkExists(String itemId) throws TapItemNotExistException {
        if (tapCache.get(itemId) == null) {
//        if (!tapCache.containsKey(itemId)) {
            throw rctItemNotExistException(itemId);
        }
    }

    private static class SetPropertiesClosure implements CacheEntryProcessor<String, TapItem, TapItem> {
        private final String id;
        private final Map<String, Object> attributes;

        public SetPropertiesClosure(String id, Map<String, Object> attributes) {
            this.id = id;
            this.attributes = attributes;
        }

        @Override
        public TapItem process(MutableEntry<String, TapItem> e, Object... arguments) throws EntryProcessorException {
            TapItem item;
            if (!e.exists()) {
                item = new TapItem(id, attributes);
            } else {
                item = e.getValue();
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    String name = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null) {
                        item.setAttribute(name, value);
                    } else {
                        item.removeAttribute(name);
                    }
                }
            }
            e.setValue(item);
            return item;
        }
    }

    private static class SetParentClosure implements CacheEntryProcessor<String, TapItem, String> {
        private final String parentId;

        private SetParentClosure(String parentId) {
            this.parentId = parentId;
        }

        @Override
        public String process(MutableEntry<String, TapItem> entry, Object... arguments) throws EntryProcessorException {
            String result = null;
            if (entry.exists()) {
                TapItem tapItem = entry.getValue();
                result = tapItem.getParentId();
                if (result == null) {
                    tapItem.setParentId(parentId);
                    entry.setValue(tapItem);
                }
            }
            return result;
        }
    }

    private static class RemoveParentClosure implements CacheEntryProcessor<String, TapItem, String> {
        protected final String expectedParentId;

        protected RemoveParentClosure(String expectedParentId) {
            this.expectedParentId = expectedParentId;
        }

        @Override
        public String process(MutableEntry<String, TapItem> entry, Object... arguments) throws EntryProcessorException {
            String parentId = null;
            if (entry.exists()) {
                TapItem tapItem = entry.getValue();
                parentId = tapItem.getParentId();
                if (parentId == null || parentId.equals(expectedParentId)) {
                    tapItem.setParentId(null);
                    entry.setValue(tapItem);
                }
            }
            return parentId;
        }
    }

    private static class AddChildClosure implements CacheEntryProcessor<String, Set<String>, Collection<String>> {
        private final String childId;

        private AddChildClosure(String childId) {
            this.childId = childId;
        }

        @Override
        public Collection<String> process(MutableEntry<String, Set<String>> entry, Object... arguments) throws EntryProcessorException {
            Set<String> result = entry.getValue();
            if (result == null) {
                result = new HashSet<>();
            }
            result.add(childId);
            entry.setValue(result);
            return result;
        }
    }

    private static class RemoveChildClosure implements CacheEntryProcessor<String, Set<String>, Collection<String>> {
        protected final String childId;

        protected RemoveChildClosure(String childId) {
            this.childId = childId;
        }

        @Override
        public Collection<String> process(MutableEntry<String, Set<String>> entry, Object... arguments) throws EntryProcessorException {
            Set<String> children = entry.getValue();
            if (children == null || children.isEmpty()) {
                return null;
            }
            children.remove(childId);
            if (children.isEmpty()) {
                entry.remove();
                return Collections.emptySet();
            }
            entry.setValue(children);
            return children;
        }
    }
}
