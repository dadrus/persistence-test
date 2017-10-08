package eu.drus.jpa.unit.suite;

import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

public class Neo4jManager {

    private static ServerControls server;

    public synchronized static void startServer() {
        if (server == null) {
            server = TestServerBuilders.newInProcessBuilder().withConfig("dbms.connector.bolt.address", "localhost:7687").newServer();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    server.close();
                }
            });
        }
    }

}
