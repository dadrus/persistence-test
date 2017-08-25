package eu.drus.jpa.unit.concordion;

import static eu.drus.jpa.unit.util.ReflectionUtils.getValue;
import static eu.drus.jpa.unit.util.ReflectionUtils.injectValue;

import java.lang.reflect.Field;

import org.concordion.api.Fixture;
import org.concordion.api.Resource;
import org.concordion.api.SpecificationLocator;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.internal.ClassNameAndTypeBasedSpecificationLocator;
import org.junit.runners.model.InitializationError;

import eu.drus.jpa.unit.concordion.internal.ConcordionInterceptor;
import eu.drus.jpa.unit.concordion.internal.JpaUnitFixture;
import eu.drus.jpa.unit.spi.DecoratorExecutor;
import net.sf.cglib.proxy.Callback;
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
        final Field firstTestSuperField = getClass().getSuperclass().getDeclaredField("firstTest");
        final Field setupFixtureField = getClass().getSuperclass().getDeclaredField("setupFixture");

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

    @Override
    protected SpecificationLocator getSpecificationLocator() {
        return new ClassNameAndTypeBasedSpecificationLocator() {
            @Override
            public Resource locateSpecification(final Object fixtureObject, final String typeSuffix) {
                return super.locateSpecification(fixtureObject instanceof Factory ? getDelegate(fixtureObject) : fixtureObject, typeSuffix);
            }

            private Object getDelegate(final Object fixtureObject) {
                final Callback callback = ((Factory) fixtureObject).getCallback(0);
                return ((ConcordionInterceptor) callback).getDelegate();
            }
        };

    }
}
