package eu.drus.jpa.unit.neo4j;

import java.sql.Connection;

import eu.drus.jpa.unit.neo4j.ext.ConfigurationRegistry;
import eu.drus.jpa.unit.spi.ExecutionContext;
import eu.drus.jpa.unit.spi.TestMethodDecorator;
import eu.drus.jpa.unit.spi.TestMethodInvocation;

public class Neo4JDbDecorator implements TestMethodDecorator {

    private ConfigurationRegistry configurationRegistry = new ConfigurationRegistry();

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public boolean isConfigurationSupported(final ExecutionContext ctx) {
        return configurationRegistry.hasConfiguration(ctx.getDescriptor());
    }

    @Override
    public void beforeTest(final TestMethodInvocation invocation) throws Exception {
        final ExecutionContext context = invocation.getContext();

        final Neo4JDbFeatureExecutor dbFeatureExecutor = new Neo4JDbFeatureExecutor(invocation.getFeatureResolver());
        final Connection connection = (Connection) context.getData("gds");
        dbFeatureExecutor.executeBeforeTest(connection);
    }

    @Override
    public void afterTest(final TestMethodInvocation invocation) throws Exception {
        final ExecutionContext context = invocation.getContext();

        final Neo4JDbFeatureExecutor dbFeatureExecutor = new Neo4JDbFeatureExecutor(invocation.getFeatureResolver());
        final Connection connection = (Connection) context.getData("gds");
        dbFeatureExecutor.executeAfterTest(connection, invocation.hasErrors());
    }

}
