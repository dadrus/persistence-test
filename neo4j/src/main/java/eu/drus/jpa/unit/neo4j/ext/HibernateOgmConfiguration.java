package eu.drus.jpa.unit.neo4j.ext;

import java.util.Map;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import eu.drus.jpa.unit.spi.PersistenceUnitDescriptor;

public class HibernateOgmConfiguration implements Configuration {

    // hibernate.ogm.neo4j.database_path
    // hibernate.ogm.neo4j.configuration_resource_name
    // hibernate.schema_update.unique_constraint_strategy

    private static final String HIBERNATE_OGM_DATASTORE_PASSWORD = "hibernate.ogm.datastore.password";
    private static final String HIBERNATE_OGM_DATASTORE_USERNAME = "hibernate.ogm.datastore.username";
    private static final String HIBERNATE_OGM_DATASTORE_HOST = "hibernate.ogm.datastore.host";
    private static final String HIBERNATE_OGM_DATASTORE_PROVIDER = "hibernate.ogm.datastore.provider";

    public static class ConfigurationFactoryImpl implements ConfigurationFactory {

        @Override
        public boolean isSupported(final PersistenceUnitDescriptor descriptor) {
            final Map<String, Object> dbConfig = descriptor.getProperties();

            return dbConfig.containsKey(HIBERNATE_OGM_DATASTORE_PROVIDER)
                    && ((String) dbConfig.get(HIBERNATE_OGM_DATASTORE_PROVIDER)).contains("neo4j_bolt");
        }

        @Override
        public Configuration createConfiguration(final PersistenceUnitDescriptor descriptor) {
            return new HibernateOgmConfiguration(descriptor);
        }
    }

    private Driver driver;

    private HibernateOgmConfiguration(final PersistenceUnitDescriptor descriptor) {
        final Map<String, Object> properties = descriptor.getProperties();

        final String host = (String) properties.get(HIBERNATE_OGM_DATASTORE_HOST);
        final String user = (String) properties.get(HIBERNATE_OGM_DATASTORE_USERNAME);
        final String password = (String) properties.get(HIBERNATE_OGM_DATASTORE_PASSWORD);

        if (user != null && password != null) {
            driver = GraphDatabase.driver("bolt://" + host, AuthTokens.basic(user, password));
        } else {
            driver = GraphDatabase.driver(host);
        }
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

}
