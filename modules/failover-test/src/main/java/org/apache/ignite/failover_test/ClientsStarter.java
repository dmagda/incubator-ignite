package org.apache.ignite.failover_test;

import org.apache.ignite.*;

/**
 * For the failover test start with the following settings:
 * - VM arguments: -DIGNITE_QUIET=false
 * - Program arguments: modules/failover-test/config/rcg-client.xml
 */
public class ClientsStarter {
    public static void main(String[] args) throws Exception {
        String cfg = args.length > 0 ? args[0] : "modules/rc-test/config/rcg-client.xml";

        try (Ignite ignite = Ignition.start(cfg)) {
            LoadRunner runner = new LoadRunner(ignite);

            runner.loadInitialData();

            runner.run();
        }
    }
}
