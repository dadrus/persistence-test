package eu.drus.jpa.unit.test;

import javax.naming.NamingException;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import eu.drus.jpa.unit.suite.Neo4jManager;

@RunWith(Cucumber.class)
@CucumberOptions(strict = false, format = {
        "pretty", "html:target/site/cucumber-pretty", "json:target/cucumber.json"
}, tags = {
        "~@ignore"
}, features = "classpath:bdd-features", glue = "classpath:eu.drus.jpa.unit.test.cucumber.cdi_glue")
public class CucumberCdiTest {

    private static CdiContainer cdiContainer;

    @BeforeClass
    public static void startContainer() throws NamingException {
        startNeo4j();
        cdiContainer = CdiContainerLoader.getCdiContainer();
        cdiContainer.boot();
    }

    @AfterClass
    public static void stopContainer() {
        cdiContainer.shutdown();
    }

    public static void startNeo4j() {
        Neo4jManager.startServer();
    }
}
