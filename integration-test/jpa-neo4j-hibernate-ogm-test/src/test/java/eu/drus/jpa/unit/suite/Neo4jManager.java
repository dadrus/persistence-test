package eu.drus.jpa.unit.suite;

import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

public class Neo4jManager {

    private static Neo4jManager INSTANCE;

    private ServerControls server;

    private Neo4jManager() {
        server = TestServerBuilders.newInProcessBuilder().withConfig("dbms.connector.bolt.address", "localhost:7687").newServer();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.close();
            }
        });
    }

    public synchronized static void startServer() {
        if (INSTANCE == null) {
            INSTANCE = new Neo4jManager();
        }
    }

}
