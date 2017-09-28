package eu.drus.jpa.unit.neo4j.ext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import eu.drus.jpa.unit.spi.PersistenceUnitDescriptor;

public class HibernateOgmConfiguration implements Configuration {

    // hibernate.ogm.neo4j.database_path
    // hibernate.ogm.neo4j.configuration_resource_name
    // hibernate.schema_update.unique_constraint_strategy

    protected static final String HIBERNATE_OGM_DATASTORE_PASSWORD = "hibernate.ogm.datastore.password";
    protected static final String HIBERNATE_OGM_DATASTORE_USERNAME = "hibernate.ogm.datastore.username";
    protected static final String HIBERNATE_OGM_DATASTORE_HOST = "hibernate.ogm.datastore.host";
    protected static final String HIBERNATE_OGM_DATASTORE_PROVIDER = "hibernate.ogm.datastore.provider";

    public static class ConfigurationFactoryImpl implements ConfigurationFactory {

        @Override
        public boolean isSupported(final PersistenceUnitDescriptor descriptor) {
            final Map<String, Object> dbConfig = descriptor.getProperties();

            final String key = (String) dbConfig.get(HIBERNATE_OGM_DATASTORE_PROVIDER);

            return key != null && (key.equals("neo4j_bolt") || key.equals("neo4j_http"));
        }

        @Override
        public Configuration createConfiguration(final PersistenceUnitDescriptor descriptor) {
            return new HibernateOgmConfiguration(descriptor);
        }
    }

    private String url;
    private String user;
    private String password;

    private HibernateOgmConfiguration(final PersistenceUnitDescriptor descriptor) {
        final Map<String, Object> properties = descriptor.getProperties();

        final String host = (String) properties.get(HIBERNATE_OGM_DATASTORE_HOST);
        user = (String) properties.get(HIBERNATE_OGM_DATASTORE_USERNAME);
        password = (String) properties.get(HIBERNATE_OGM_DATASTORE_PASSWORD);

        final String key = (String) properties.get(HIBERNATE_OGM_DATASTORE_PROVIDER);
        if (key.equals("neo4j_bolt")) {
            url = "jdbc:neo4j:bolt://" + host;
        } else if (key.equals("neo4j_http")) {
            url = "jdbc:neo4j:http://" + host;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        final Connection connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }

}
