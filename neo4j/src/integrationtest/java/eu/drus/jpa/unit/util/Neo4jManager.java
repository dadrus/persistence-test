package eu.drus.jpa.unit.util;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

public class Neo4jManager implements BeforeAllCallback {

    private static ServerControls server;

    public static synchronized void startServer() {
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

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        startServer();
    }

}
