package eu.drus.jpa.unit.test;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import eu.drus.jpa.unit.api.JpaUnitRunner;
import eu.drus.jpa.unit.util.Neo4jManager;

@RunWith(JpaUnitRunner.class)
public class TransactionTest extends AbstractTransactionTest {

    @BeforeClass
    public static void startNeo4j() {
        Neo4jManager.startServer();
    }
}
