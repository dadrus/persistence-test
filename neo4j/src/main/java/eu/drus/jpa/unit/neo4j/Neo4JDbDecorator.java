package eu.drus.jpa.unit.neo4j;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;

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
        final Driver driver = (Driver) context.getData("gds");
        final Session session = driver.session();
        context.storeData("session", session);
        dbFeatureExecutor.executeBeforeTest(session);
    }

    @Override
    public void afterTest(final TestMethodInvocation invocation) throws Exception {
        final ExecutionContext context = invocation.getContext();

        final Neo4JDbFeatureExecutor dbFeatureExecutor = new Neo4JDbFeatureExecutor(invocation.getFeatureResolver());
        final Session session = (Session) context.getData("session");
        try {
            dbFeatureExecutor.executeAfterTest(session, invocation.hasErrors());
        } finally {
            session.close();
        }
    }

}
