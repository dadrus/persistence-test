package eu.drus.jpa.unit.test;

import org.concordion.api.BeforeSuite;
import org.junit.runner.RunWith;

import eu.drus.jpa.unit.api.concordion.JpaUnitConcordionRunner;
import eu.drus.jpa.unit.suite.Neo4jManager;

@RunWith(JpaUnitConcordionRunner.class)
public class NewAccountFixture extends AbstractNewAccountFixture {

    @BeforeSuite
    public static void startNeo4j() {
        Neo4jManager.startServer();
    }
}
