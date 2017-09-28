package eu.drus.jpa.unit.neo4j;

import com.zaxxer.hikari.HikariDataSource;

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
        ctx.storeData(Constants.KEY_DATA_SOURCE, configuration.createDataSource());
    }

    @Override
    public void afterAll(final ExecutionContext ctx, final Class<?> testClass) throws Exception {
        final HikariDataSource ds = (HikariDataSource) ctx.getData(Constants.KEY_DATA_SOURCE);
        ds.close();
        ctx.storeData(Constants.KEY_DATA_SOURCE, null);
    }

}
