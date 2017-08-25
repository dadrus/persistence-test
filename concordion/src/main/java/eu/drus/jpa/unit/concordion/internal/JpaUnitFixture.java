package eu.drus.jpa.unit.concordion.internal;

import org.concordion.internal.FixtureInstance;

import eu.drus.jpa.unit.api.JpaUnitException;
import eu.drus.jpa.unit.core.JpaUnitContext;
import eu.drus.jpa.unit.spi.DecoratorExecutor;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Factory;

public class JpaUnitFixture extends FixtureInstance {

    private DecoratorExecutor executor;
    private Object originalFixture;

    public JpaUnitFixture(final DecoratorExecutor executor, final Object fixtureObject) {
        super(fixtureObject);
        originalFixture = getDelegate(fixtureObject);
        this.executor = executor;
    }

    @Override
    public void beforeSuite() {
        try {
            executor.processBeforeAll(JpaUnitContext.getInstance(originalFixture.getClass()), originalFixture.getClass());
        } catch (final Exception e) {
            throw new JpaUnitException(e);
        }
        super.beforeSuite();
    }

    @Override
    public void afterSuite() {
        try {
            executor.processAfterAll(JpaUnitContext.getInstance(originalFixture.getClass()), originalFixture.getClass());
        } catch (final Exception e) {
            throw new JpaUnitException(e);
        }
        super.afterSuite();
    }

    private static Object getDelegate(final Object fixtureObject) {
        final Callback callback = ((Factory) fixtureObject).getCallback(0);
        return ((ConcordionInterceptor) callback).getDelegate();
    }
}
