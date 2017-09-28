package eu.drus.jpa.unit.neo4j.ext;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.drus.jpa.unit.spi.PersistenceUnitDescriptor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        HibernateOgmConfiguration.class, DriverManager.class
})
public class HibernateOgmConfigurationTest {

    @Mock
    private Connection conneciton;

    @Mock
    private PersistenceUnitDescriptor descriptor;

    @Before
    public void prepareJpaUnitContext() throws SQLException {
        mockStatic(DriverManager.class);
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(conneciton);
    }

    @Test
    public void testNeo4JBoltProtocolSupport() {
        // GIVEN
        final Map<String, Object> dbConfig = new HashMap<>();
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_PROVIDER, "neo4j_bolt");
        when(descriptor.getProperties()).thenReturn(dbConfig);
        final ConfigurationFactory factory = new HibernateOgmConfiguration.ConfigurationFactoryImpl();

        // WHEN
        final boolean isSupported = factory.isSupported(descriptor);

        // THEN
        assertTrue(isSupported);
    }

    @Test
    public void testNeo4JHttpProtocolSupport() {
        // GIVEN
        final Map<String, Object> dbConfig = new HashMap<>();
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_PROVIDER, "neo4j_http");
        when(descriptor.getProperties()).thenReturn(dbConfig);
        final ConfigurationFactory factory = new HibernateOgmConfiguration.ConfigurationFactoryImpl();

        // WHEN
        final boolean isSupported = factory.isSupported(descriptor);

        // THEN
        assertTrue(isSupported);
    }

    @Test
    public void testNeo4JUnsupportedProtocol() {
        // GIVEN
        final Map<String, Object> dbConfig = new HashMap<>();
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_PROVIDER, "neo4j_foo");
        when(descriptor.getProperties()).thenReturn(dbConfig);
        final ConfigurationFactory factory = new HibernateOgmConfiguration.ConfigurationFactoryImpl();

        // WHEN
        final boolean isSupported = factory.isSupported(descriptor);

        // THEN
        assertFalse(isSupported);
    }

    @Test
    public void testNeo4JUnsupportedConfiguration() {
        // GIVEN
        final Map<String, Object> dbConfig = new HashMap<>();
        dbConfig.put("foo", "neo4j_http");
        when(descriptor.getProperties()).thenReturn(dbConfig);
        final ConfigurationFactory factory = new HibernateOgmConfiguration.ConfigurationFactoryImpl();

        // WHEN
        final boolean isSupported = factory.isSupported(descriptor);

        // THEN
        assertFalse(isSupported);
    }

    @Test
    public void testOpenHttpConnection() throws SQLException {
        // GIVEN
        final Map<String, Object> dbConfig = new HashMap<>();
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_PROVIDER, "neo4j_http");
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_HOST, "localhost:7474");
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_USERNAME, "neo4j");
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_PASSWORD, "test");

        when(descriptor.getProperties()).thenReturn(dbConfig);
        final ConfigurationFactory factory = new HibernateOgmConfiguration.ConfigurationFactoryImpl();
        final Configuration configuration = factory.createConfiguration(descriptor);

        // WHEN
        final Connection connection = configuration.getConnection();

        // THEN
        assertNotNull(connection);

        final ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> passCaptor = ArgumentCaptor.forClass(String.class);
        verifyStatic(DriverManager.class, times(1));
        DriverManager.getConnection(urlCaptor.capture(), userCaptor.capture(), passCaptor.capture());

        assertThat(urlCaptor.getValue(), equalTo("jdbc:neo4j:http://localhost:7474"));
        assertThat(userCaptor.getValue(), equalTo("neo4j"));
        assertThat(passCaptor.getValue(), equalTo("test"));
    }

    @Test
    public void testOpenBoltConnection() throws SQLException {
        // GIVEN
        final Map<String, Object> dbConfig = new HashMap<>();
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_PROVIDER, "neo4j_bolt");
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_HOST, "localhost:7687");
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_USERNAME, "neo4j");
        dbConfig.put(HibernateOgmConfiguration.HIBERNATE_OGM_DATASTORE_PASSWORD, "test");

        when(descriptor.getProperties()).thenReturn(dbConfig);
        final ConfigurationFactory factory = new HibernateOgmConfiguration.ConfigurationFactoryImpl();
        final Configuration configuration = factory.createConfiguration(descriptor);

        // WHEN
        final Connection connection = configuration.getConnection();

        // THEN
        assertNotNull(connection);

        final ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> passCaptor = ArgumentCaptor.forClass(String.class);
        verifyStatic(DriverManager.class, times(1));
        DriverManager.getConnection(urlCaptor.capture(), userCaptor.capture(), passCaptor.capture());

        assertThat(urlCaptor.getValue(), equalTo("jdbc:neo4j:bolt://localhost:7687"));
        assertThat(userCaptor.getValue(), equalTo("neo4j"));
        assertThat(passCaptor.getValue(), equalTo("test"));
    }
}
