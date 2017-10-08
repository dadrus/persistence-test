package eu.drus.jpa.unit.suite;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import eu.drus.jpa.unit.features.CucumberCdiFeature;
import eu.drus.jpa.unit.features.CucumberFeature;
import eu.drus.jpa.unit.fixtures.BankingFixture;
import eu.drus.jpa.unit.test.ApplyCustomScripsIT;
import eu.drus.jpa.unit.test.CdiEnabledJpaUnitIT;
import eu.drus.jpa.unit.test.CdiWithJpaIT;
import eu.drus.jpa.unit.test.CleanupCacheIT;
import eu.drus.jpa.unit.test.CleanupIT;
import eu.drus.jpa.unit.test.CleanupUsingScriptIT;
import eu.drus.jpa.unit.test.ExpectedDataSetsIT;
import eu.drus.jpa.unit.test.InitialDataSetsIT;
import eu.drus.jpa.unit.test.BootsrappingIT;
import eu.drus.jpa.unit.test.TransactionalJunit5IT;
import eu.drus.jpa.unit.util.Neo4jManager;
import eu.drus.jpa.unit.test.TransactionalIT;

@RunWith(Suite.class)
@SuiteClasses({
        ApplyCustomScripsIT.class, BankingFixture.class, CdiEnabledJpaUnitIT.class, CdiWithJpaIT.class, CleanupCacheIT.class,
        CleanupIT.class, CleanupUsingScriptIT.class, CucumberCdiFeature.class, CucumberFeature.class, ExpectedDataSetsIT.class,
        InitialDataSetsIT.class, BootsrappingIT.class, TransactionalIT.class, TransactionalJunit5IT.class
})
public class Neo4jSuite {

    @BeforeClass
    public static void startMongod() {
        Neo4jManager.startServer();
    }
}
