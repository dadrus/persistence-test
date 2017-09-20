package eu.drus.jpa.unit.concordion;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import eu.drus.jpa.unit.api.CleanupPhase;
import eu.drus.jpa.unit.core.JpaUnitContext;
import eu.drus.jpa.unit.spi.DecoratorExecutor;
import eu.drus.jpa.unit.spi.ExecutionContext;
import eu.drus.jpa.unit.spi.FeatureResolver;
import eu.drus.jpa.unit.spi.TestInvocation;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ConcordionInterceptor implements MethodInterceptor {

    private final DecoratorExecutor executor;
    private Object delegate;

    public ConcordionInterceptor(final DecoratorExecutor executor, final Object delegate) {
        this.executor = executor;
        this.delegate = delegate;
    }

    private static boolean isObjectMethod(final Method method) {
        switch (method.getName()) {
        case "hashCode":
        case "toString":
        case "clone":
            return true;
        default:
            return false;
        }
    }

    private static boolean hasConcordionAnnotations(final Method method) {
        final Annotation[] annotations = method.getDeclaredAnnotations();
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.getName().startsWith("org.concordion.api")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
        if (isObjectMethod(method) || hasConcordionAnnotations(method)) {
            return methodProxy.invoke(delegate, args);
        }

        final FeatureResolver resolver = FeatureResolver.newFeatureResolver(delegate.getClass()).withTestMethod(method)
                .withDefaultCleanupPhase(CleanupPhase.NONE).build();

        Object result = null;
        final TestMethodInvocationImpl invocation = new TestMethodInvocationImpl(delegate, method, resolver);
        executor.processBefore(invocation);
        try {
            result = methodProxy.invoke(delegate, args);
        } catch (final Exception e) {
            invocation.setTestException(e);
            executor.processAfter(invocation);
            throw e;
        }
        executor.processAfter(invocation);

        return result;
    }

    private static class TestMethodInvocationImpl implements TestInvocation {

        private final Object instance;
        private final Class<?> clazz;
        private final Method method;
        private final JpaUnitContext ctx;
        private Exception e;
        private FeatureResolver resolver;

        private TestMethodInvocationImpl(final Object instance, final Method method, final FeatureResolver resolver) {
            this.instance = instance;
            clazz = instance.getClass();
            this.method = method;
            this.resolver = resolver;
            ctx = JpaUnitContext.getInstance(clazz);
        }

        @Override
        public Class<?> getTestClass() {
            return clazz;
        }

        @Override
        public Optional<Method> getTestMethod() {
            return Optional.of(method);
        }

        @Override
        public ExecutionContext getContext() {
            return ctx;
        }

        @Override
        public Optional<Throwable> getException() {
            return Optional.ofNullable(e);
        }

        public void setTestException(final Exception e) {
            this.e = e;
        }

        @Override
        public FeatureResolver getFeatureResolver() {
            return resolver;
        }

        @Override
        public Optional<Object> getTestInstance() {
            return Optional.of(instance);
        }
    }

    public Object getDelegate() {
        return delegate;
    }

}
