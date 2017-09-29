package eu.drus.jpa.unit.test;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import eu.drus.jpa.unit.suite.Neo4jManager;

@RunWith(CdiTestRunner.class)
public class CdiWithJpaTest extends AbstractCdiWithJpaTest {

    @BeforeClass
    public static void startNeo4j() {
        Neo4jManager.startServer();
    }
}
