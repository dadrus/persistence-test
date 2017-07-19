package eu.drus.jpa.unit.decorator.jpa;

import javax.persistence.EntityManagerFactory;

import eu.drus.jpa.unit.spi.Constants;
import eu.drus.jpa.unit.spi.ExecutionContext;
import eu.drus.jpa.unit.spi.TestMethodDecorator;
import eu.drus.jpa.unit.spi.TestMethodInvocation;

public class SecondLevelCacheDecorator implements TestMethodDecorator {

    @Override
    public int getPriority() {
        return 0;
    }

    private void evictCache(final boolean doEvict, final EntityManagerFactory emf) {
        if (doEvict) {
            emf.getCache().evictAll();
        }
    }

    @Override
    public void processInstance(final Object instance, final TestMethodInvocation invocation) throws Exception {
        // nothing to do
    }

    @Override
    public void beforeTest(final TestMethodInvocation invocation) throws Exception {
        final EntityManagerFactory emf = (EntityManagerFactory) invocation.getContext().getData(Constants.KEY_ENTITY_MANAGER_FACTORY);

        evictCache(invocation.getFeatureResolver().shouldEvictCacheBefore(), emf);
    }

    @Override
    public void afterTest(final TestMethodInvocation invocation) throws Exception {
        final EntityManagerFactory emf = (EntityManagerFactory) invocation.getContext().getData(Constants.KEY_ENTITY_MANAGER_FACTORY);

        evictCache(invocation.getFeatureResolver().shouldEvictCacheAfter(), emf);
    }

    @Override
    public boolean isConfigurationSupported(final ExecutionContext ctx) {
        return true;
    }

}
