package eu.drus.jpa.unit.test;

import static org.junit.Assert.assertNotNull;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import eu.drus.jpa.unit.api.ExpectedDataSets;
import eu.drus.jpa.unit.api.InitialDataSets;
import eu.drus.jpa.unit.api.JpaUnit;
import eu.drus.jpa.unit.api.TransactionMode;
import eu.drus.jpa.unit.api.Transactional;
import eu.drus.jpa.unit.test.model.Account;
import eu.drus.jpa.unit.test.model.Depositor;
import eu.drus.jpa.unit.test.model.GiroAccount;
import eu.drus.jpa.unit.test.model.InstantAccessAccount;
import eu.drus.jpa.unit.test.model.OperationNotSupportedException;
import eu.drus.jpa.unit.util.Neo4jManager;

@ExtendWith(Neo4jManager.class)
@ExtendWith(JpaUnit.class)
@RunWith(JUnitPlatform.class)
public class TransactionalJunit5IT {

    @PersistenceContext(unitName = "my-test-unit")
    private EntityManager manager;

    // Hibernate OGM always requires an active transaction for dbs supporting transactions
    // See also https://hibernate.atlassian.net/browse/OGM-1322

    @Test
    @InitialDataSets("datasets/initial-data.xml")
    @ExpectedDataSets("datasets/initial-data.xml")
    @Transactional(TransactionMode.ROLLBACK)
    public void transactionRollbackTest() {
        final Depositor entity = manager.find(Depositor.class, 106L);

        assertNotNull(entity);
        entity.setName("Alex");
    }

    @Test
    @InitialDataSets("datasets/initial-data.xml")
    @ExpectedDataSets("datasets/expected-data.xml")
    @Transactional(TransactionMode.COMMIT)
    public void transactionCommitTest() throws OperationNotSupportedException {
        final Depositor entity = manager.find(Depositor.class, 106L);

        assertNotNull(entity);
        entity.setName("Max");

        final Set<Account> accounts = entity.getAccounts();

        final GiroAccount giroAccount = accounts.stream().filter(a -> a instanceof GiroAccount).map(a -> (GiroAccount) a).findFirst().get();
        final InstantAccessAccount accessAcount = accounts.stream().filter(a -> a instanceof InstantAccessAccount)
                .map(a -> (InstantAccessAccount) a).findFirst().get();

        giroAccount.deposit(100.0f);
        giroAccount.transfer(150.0f, accessAcount);
    }
}