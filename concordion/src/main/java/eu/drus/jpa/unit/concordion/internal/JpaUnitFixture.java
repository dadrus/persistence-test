package eu.drus.jpa.unit.concordion.internal;

import org.concordion.internal.FixtureInstance;

import eu.drus.jpa.unit.api.JpaUnitException;
import eu.drus.jpa.unit.core.JpaUnitContext;
import eu.drus.jpa.unit.spi.DecoratorExecutor;

public class JpaUnitFixture extends FixtureInstance {

    private DecoratorExecutor executor;

    public JpaUnitFixture(final DecoratorExecutor executor, final Object fixtureObject) {
        super(fixtureObject);
        this.executor = executor;
    }

    @Override
    public void beforeSuite() {
        try {
            executor.processBeforeAll(JpaUnitContext.getInstance(getFixtureClass()), getFixtureClass());
        } catch (final Exception e) {
            throw new JpaUnitException(e);
        }
        super.beforeSuite();
    }

    @Override
    public void afterSuite() {
        try {
            executor.processAfterAll(JpaUnitContext.getInstance(getFixtureClass()), getFixtureClass());
        } catch (final Exception e) {
            throw new JpaUnitException(e);
        }
        super.afterSuite();
    }
}
