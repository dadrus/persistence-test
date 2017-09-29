package eu.drus.jpa.unit.neo4j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.zaxxer.hikari.HikariDataSource;

import eu.drus.jpa.unit.neo4j.ext.ConfigurationRegistry;
import eu.drus.jpa.unit.spi.ExecutionContext;
import eu.drus.jpa.unit.spi.TestMethodInvocation;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Neo4JDbDecorator.class)
public class Neo4JDbDecoratorTest {

    @Mock
    private ConfigurationRegistry configRegistry;

    @Mock
    private HikariDataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private TestMethodInvocation invocation;

    @Mock
    private ExecutionContext ctx;

    @Mock
    private Neo4JDbFeatureExecutor executor;

    private Neo4JDbDecorator decorator;

    @Before
    public void prepareTest() throws Exception {
        whenNew(ConfigurationRegistry.class).withAnyArguments().thenReturn(configRegistry);
        whenNew(Neo4JDbFeatureExecutor.class).withAnyArguments().thenReturn(executor);

        when(invocation.getContext()).thenReturn(ctx);
        when(dataSource.getConnection()).thenReturn(connection);
        when(ctx.getData(eq(Constants.KEY_DATA_SOURCE))).thenReturn(dataSource);
        when(ctx.getData(eq(Constants.KEY_CONNECTION))).thenReturn(connection);

        decorator = new Neo4JDbDecorator();
    }

    @Test
    public void testRequiredPriority() {
        // GIVEN

        // WHEN
        final int priority = decorator.getPriority();

        // THEN
        assertThat(priority, equalTo(5));
    }

    @Test
    public void testBeforeTest() throws Exception {
        // GIVEN

        // WHEN
        decorator.beforeTest(invocation);

        // THEN
        verify(connection).setAutoCommit(eq(Boolean.FALSE));
        verify(executor).executeBeforeTest(eq(connection));
        verify(ctx).storeData(eq(Constants.KEY_CONNECTION), eq(connection));
    }

    @Test
    public void testAfterTest() throws Exception {
        // GIVEN

        // WHEN
        decorator.afterTest(invocation);

        // THEN
        verify(executor).executeAfterTest(eq(connection), eq(Boolean.FALSE));
        verify(ctx).storeData(eq(Constants.KEY_CONNECTION), isNull());
        verifyZeroInteractions(dataSource);
    }
}
