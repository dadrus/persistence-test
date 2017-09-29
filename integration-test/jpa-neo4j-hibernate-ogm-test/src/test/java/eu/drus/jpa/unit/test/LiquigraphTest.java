package eu.drus.jpa.unit.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liquigraph.core.api.Liquigraph;
import org.liquigraph.core.configuration.Configuration;
import org.liquigraph.core.configuration.ConfigurationBuilder;

import eu.drus.jpa.unit.api.Bootstrapping;
import eu.drus.jpa.unit.api.JpaUnitRunner;
import eu.drus.jpa.unit.suite.Neo4jManager;
import eu.drus.jpa.unit.test.model.Account;
import eu.drus.jpa.unit.test.model.Address;
import eu.drus.jpa.unit.test.model.ContactDetail;
import eu.drus.jpa.unit.test.model.ContactType;
import eu.drus.jpa.unit.test.model.Depositor;
import eu.drus.jpa.unit.test.model.GiroAccount;

@RunWith(JpaUnitRunner.class)
public class LiquigraphTest {

    @BeforeClass
    public static void startNeo4j() {
        Neo4jManager.startServer();
    }

    @PersistenceContext(unitName = "my-verification-unit")
    private EntityManager manager;

    @Bootstrapping
    public static void prepareDataBase(final DataSource ds) throws Exception {
        // @formatter:off
        final Configuration configuration = new ConfigurationBuilder()
                .withDataSource(ds)
                .withMasterChangelogLocation("changelog/changelog.xml")
                .withRunMode()
                .build();
        // @formatter:on

        final Liquigraph liquigraph = new Liquigraph();
        liquigraph.runMigrations(configuration);
    }

    @Test
    public void verifyDatabaseContents() {
        final TypedQuery<Depositor> query = manager.createQuery("SELECT d FROM Depositor d WHERE d.name='Max'", Depositor.class);
        final Depositor entity = query.getSingleResult();

        assertNotNull(entity);
        assertThat(entity.getName(), equalTo("Max"));
        assertThat(entity.getSurname(), equalTo("Payne"));

        final Set<ContactDetail> contactDetails = entity.getContactDetails();
        assertThat(contactDetails.size(), equalTo(1));
        final ContactDetail contactDetail = contactDetails.iterator().next();
        assertThat(contactDetail.getType(), equalTo(ContactType.EMAIL));
        assertThat(contactDetail.getValue(), equalTo("max@payne.com"));

        final Set<Address> addresses = entity.getAddresses();
        assertThat(addresses.size(), equalTo(1));
        final Address address = addresses.iterator().next();
        assertThat(address.getCountry(), equalTo("Unknown"));
        assertThat(address.getZipCode(), equalTo("111111"));
        assertThat(address.getCity(), equalTo("Unknown"));
        assertThat(address.getStreet(), equalTo("Unknown"));

        final Set<Account> accounts = entity.getAccounts();
        assertThat(accounts.size(), equalTo(1));
        final Account account = accounts.iterator().next();
        assertThat(account, instanceOf(GiroAccount.class));
        final GiroAccount giroAccount = (GiroAccount) account;
        assertThat(giroAccount.getBalance(), equalTo(100000.0));

        assertThat(giroAccount.getCreditLimit(), equalTo(100000.0));
    }
}
