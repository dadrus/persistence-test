package eu.drus.jpa.unit.concordion;

import static eu.drus.jpa.unit.util.ReflectionUtils.getValue;
import static eu.drus.jpa.unit.util.ReflectionUtils.injectValue;

import java.lang.reflect.Field;

import org.concordion.api.Fixture;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runners.model.InitializationError;

import eu.drus.jpa.unit.concordion.internal.ConcordionInterceptor;
import eu.drus.jpa.unit.concordion.internal.JpaUnitFixture;
import eu.drus.jpa.unit.spi.DecoratorExecutor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

public class JpaUnitConcordionRunner extends ConcordionRunner {

    private static DecoratorExecutor executor = new DecoratorExecutor();

    public JpaUnitConcordionRunner(final Class<?> fixtureClass) throws InitializationError {
        super(fixtureClass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object fixtureObject;
        final Field firstTestSuperField = getClass().getField("firstTest");
        final Field setupFixtureField = getClass().getField("setupFixture");

        final Fixture setupFixture = (Fixture) getValue(setupFixtureField, this);

        if ((boolean) getValue(firstTestSuperField, this)) {
            injectValue(firstTestSuperField, this, false);
            // we've already created a test object above, so reuse it to make sure we don't
            // initialise the fixture object multiple times
            fixtureObject = setupFixture.getFixtureObject();
        } else {
            // junit creates a new object for each test case, so we need to capture this
            // and setup our object - that makes sure that scoped variables are injected properly
            final Object target = super.createTest();
            fixtureObject = Enhancer.create(target.getClass(), new ConcordionInterceptor(executor, target));
        }

        // we need to setup the concordion scoped objects so that the @Before methods and @Rules can
        // access them
        setupFixture.setupForRun(fixtureObject);

        return fixtureObject;
    }

    @Override
    protected Fixture createFixture(final Object fixtureObject) {

        Object target;
        if (fixtureObject instanceof Factory) {
            target = fixtureObject;
        } else {
            target = Enhancer.create(fixtureObject.getClass(), new ConcordionInterceptor(executor, fixtureObject));
        }
        return new JpaUnitFixture(executor, target);
    }
}
