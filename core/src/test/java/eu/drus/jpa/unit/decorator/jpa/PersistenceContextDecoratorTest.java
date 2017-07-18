package eu.drus.jpa.unit.decorator.jpa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.drus.jpa.unit.spi.Constants;
import eu.drus.jpa.unit.spi.ExecutionContext;
import eu.drus.jpa.unit.spi.TestMethodInvocation;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceContextDecoratorTest {

    @Mock
    private TestMethodInvocation invocation;

    @Mock
    private ExecutionContext ctx;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unused")
    private Object someField;

    @Before
    public void setupMocks() {
        when(invocation.getContext()).thenReturn(ctx);
        when(ctx.getData(eq(Constants.KEY_ENTITY_MANAGER_FACTORY))).thenReturn(entityManagerFactory);
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    }

    @Test
    public void testApplyScopeEntityManagerInjection() throws Throwable {
        // GIVEN
        when(ctx.getData(Constants.KEY_ENTITY_MANAGER)).thenReturn(entityManager);
        final Field field = getClass().getDeclaredField("em");
        when(ctx.getPersistenceField()).thenReturn(field);
        final PersistenceContextDecorator fixture = new PersistenceContextDecorator();

        // WHEN
        fixture.processInstance(this, invocation);
        fixture.afterTest(invocation);

        // THEN
        assertThat(em, equalTo(entityManager));
        verify(entityManagerFactory).createEntityManager();
        verify(entityManager).close();

        final ArgumentCaptor<EntityManager> emCaptor = ArgumentCaptor.forClass(EntityManager.class);
        verify(ctx, times(2)).storeData(eq(Constants.KEY_ENTITY_MANAGER), emCaptor.capture());
        final List<EntityManager> captured = emCaptor.getAllValues();
        assertThat(captured.get(0), equalTo(entityManager));
        assertThat(captured.get(1), is(nullValue()));
    }

    @Test
    public void testApplyForAnUnsupportedFieldInjection() throws Throwable {
        // GIVEN
        final Field field = getClass().getDeclaredField("someField");
        when(ctx.getPersistenceField()).thenReturn(field);
        final PersistenceContextDecorator fixture = new PersistenceContextDecorator();

        // WHEN
        fixture.processInstance(this, invocation);
        fixture.afterTest(invocation);

        // THEN
        assertThat(em, nullValue());
        verify(entityManagerFactory, times(0)).createEntityManager();
        verify(entityManager, times(0)).close();
        verify(ctx, times(0)).storeData(any(String.class), any(EntityManager.class));
    }

    @Test
    public void testBeforeTestDoesNotHaveAnyEffect() throws Exception {
        // GIVEN
        final PersistenceContextDecorator fixture = new PersistenceContextDecorator();

        // WHEN
        fixture.beforeTest(invocation);

        // THEN
        verifyNoMoreInteractions(entityManagerFactory, entityManager, ctx, invocation);
    }

    @Test
    public void testRequiredPriority() {
        // GIVEN
        final PersistenceContextDecorator fixture = new PersistenceContextDecorator();

        // WHEN
        final int priority = fixture.getPriority();

        // THEN
        assertThat(priority, equalTo(2));
    }
}
