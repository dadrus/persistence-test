package eu.drus.jpa.unit.rule.transaction;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.drus.jpa.unit.rule.TestInvocation;

@RunWith(MockitoJUnitRunner.class)
public class TransactionStrategyProviderTest {

    @Mock
    private EntityTransaction tx;

    @Mock
    private TestInvocation invocation;

    private TransactionStrategyProvider provider;

    @Before
    public void createTransactionStrategyProvider() {
        provider = new TransactionStrategyProvider(tx);
    }

    @Test
    public void testTransactionRollbackStrategyExecutionForActiveTransaction() throws Throwable {
        // GIVEN
        when(tx.isActive()).thenReturn(Boolean.TRUE);
        final TransactionStrategyExecutor executor = provider.rollbackStrategy();

        // WHEN
        executor.execute(invocation);

        // THEN
        verify(tx).begin();
        verify(invocation).proceed();
        verify(tx).isActive();
        verify(tx).rollback();
        verifyNoMoreInteractions(tx);
    }

    @Test
    public void testTransactionRollbackStrategyExecutionForActiveTransactionForStatementThrowingException() throws Throwable {
        // GIVEN
        when(tx.isActive()).thenReturn(Boolean.TRUE);
        final RuntimeException error = new RuntimeException("Error while executing statement");
        doThrow(error).when(invocation).proceed();
        final TransactionStrategyExecutor executor = provider.rollbackStrategy();

        // WHEN
        try {
            executor.execute(invocation);
            fail("RuntimeException expected");
        } catch (final RuntimeException e) {
            assertThat(e, equalTo(error));
        }

        // THEN
        verify(tx).begin();
        verify(invocation).proceed();
        verify(tx).isActive();
        verify(tx).rollback();
        verifyNoMoreInteractions(tx);
    }

    @Test
    public void testTransactionRollbackStrategyExecutionWithoutActiveTransaction() throws Throwable {
        // GIVEN
        when(tx.isActive()).thenReturn(Boolean.FALSE);
        final TransactionStrategyExecutor executor = provider.rollbackStrategy();

        // WHEN
        executor.execute(invocation);

        // THEN
        verify(tx).begin();
        verify(invocation).proceed();
        verify(tx).isActive();
        verifyNoMoreInteractions(tx);
    }

    @Test
    public void testTransactionCommitStrategyExecutionForActiveTransaction() throws Throwable {
        // GIVEN
        when(tx.isActive()).thenReturn(Boolean.TRUE);
        final TransactionStrategyExecutor executor = provider.commitStrategy();

        // WHEN
        executor.execute(invocation);

        // THEN
        verify(tx).begin();
        verify(invocation).proceed();
        verify(tx).isActive();
        verify(tx).commit();
        verifyNoMoreInteractions(tx);
    }

    @Test
    public void testTransactionCommitStrategyExecutionForActiveTransactionForStatementThrowingException() throws Throwable {
        // GIVEN
        when(tx.isActive()).thenReturn(Boolean.TRUE);
        final RuntimeException error = new RuntimeException("Error while executing statement");
        doThrow(error).when(invocation).proceed();
        when(tx.isActive()).thenReturn(Boolean.TRUE);
        final TransactionStrategyExecutor executor = provider.commitStrategy();

        // WHEN
        try {
            executor.execute(invocation);
            fail("RuntimeException expected");
        } catch (final RuntimeException e) {
            assertThat(e, equalTo(error));
        }

        // THEN
        verify(tx).begin();
        verify(invocation).proceed();
        verify(tx).isActive();
        verify(tx).commit();
        verifyNoMoreInteractions(tx);
    }

    @Test
    public void testTransactionCommitStrategyExecutionWithoutActiveTransaction() throws Throwable {
        // GIVEN
        when(tx.isActive()).thenReturn(Boolean.FALSE);
        final TransactionStrategyExecutor executor = provider.commitStrategy();

        // WHEN
        executor.execute(invocation);

        // THEN
        verify(tx).begin();
        verify(invocation).proceed();
        verify(tx).isActive();
        verify(tx, times(0)).commit();
        verifyNoMoreInteractions(tx);
    }

    @Test
    public void testNoTransactionStrategyExecution() throws Throwable {
        // GIVEN
        final TransactionStrategyExecutor executor = provider.disabledStrategy();

        // WHEN
        executor.execute(invocation);

        // THEN
        verify(invocation).proceed();
        verifyZeroInteractions(tx);
    }
}
