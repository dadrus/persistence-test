package eu.drus.jpa.unit.neo4j;

import java.net.URI;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.config.Configuration;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

public class CleanupStrategyProviderTest {
    // TODO: implement me

    @BeforeClass
    public static void startNeo4j() {
        final ServerControls server = TestServerBuilders.newInProcessBuilder().withConfig("dbms.connector.bolt.address", "localhost:7687")
                .newServer();
        final Configuration config = server.config();
        final URI boltURI = server.boltURI();
        final URI httpURI = server.httpURI();
        server.close();
    }

    @Test
    public void test() {

    }
}
