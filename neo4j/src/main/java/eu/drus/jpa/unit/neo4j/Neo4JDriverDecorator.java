package eu.drus.jpa.unit.neo4j;

import org.neo4j.driver.v1.Driver;

import eu.drus.jpa.unit.neo4j.ext.Configuration;
import eu.drus.jpa.unit.neo4j.ext.ConfigurationRegistry;
import eu.drus.jpa.unit.spi.ExecutionContext;
import eu.drus.jpa.unit.spi.TestClassDecorator;

public class Neo4JDriverDecorator implements TestClassDecorator {

    private ConfigurationRegistry configurationRegistry = new ConfigurationRegistry();

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean isConfigurationSupported(final ExecutionContext ctx) {
        return configurationRegistry.hasConfiguration(ctx.getDescriptor());
    }

    @Override
    public void beforeAll(final ExecutionContext ctx, final Class<?> testClass) throws Exception {
        final Configuration configuration = configurationRegistry.getConfiguration(ctx.getDescriptor());
        ctx.storeData("gds", configuration.getDriver());
    }

    @Override
    public void afterAll(final ExecutionContext ctx, final Class<?> testClass) throws Exception {
        final Driver driver = (Driver) ctx.getData("gds");
        driver.close();
        ctx.storeData("gds", null);
    }

}
