package eu.drus.jpa.unit.test;

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
}, features = "classpath:bdd-features", glue = "classpath:eu.drus.jpa.unit.test.cucumber.glue")
public class CucumberTest {

    @BeforeClass
    public static void startNeo4j() {
        Neo4jManager.startServer();
    }

}
