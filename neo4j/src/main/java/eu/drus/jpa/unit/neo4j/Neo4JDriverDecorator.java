package eu.drus.jpa.unit.neo4j;

import java.sql.Connection;

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
        ctx.storeData("gds", configuration.getConnection());
    }

    @Override
    public void afterAll(final ExecutionContext ctx, final Class<?> testClass) throws Exception {
        final Connection connection = (Connection) ctx.getData("gds");
        connection.close();
        ctx.storeData("gds", null);
    }

}
