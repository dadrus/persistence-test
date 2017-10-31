package eu.drus.jpa.unit.test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import eu.drus.jpa.unit.api.ExpectedDataSets;
import eu.drus.jpa.unit.api.JpaUnitRunner;
import eu.drus.jpa.unit.test.model.CookingRecipe;
import eu.drus.jpa.unit.test.model.Person;
import eu.drus.jpa.unit.test.model.Technology;
import eu.drus.jpa.unit.util.Neo4jManager;

@RunWith(JpaUnitRunner.class)
public class ExpectedDataSetsIT {

    @BeforeClass
    public static void startNeo4j() {
        Neo4jManager.startServer();
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @PersistenceContext(unitName = "my-test-unit")
    protected EntityManager manager;

    protected Person person;

    @Before
    public void createTestData() {
        person = new Person("Max", "Doe");
        person.addExpertiseIn(new Technology("All kinds of weapons"));
        person.addExpertiseIn(new Technology("Detective work"));
    }

    @Test
    @ExpectedDataSets(value = "datasets/no-data.xml")
    public void test1() {
        manager.persist(person);

        expectedException.expect(AssertionError.class);
    }

    @Test
    @ExpectedDataSets(value = "datasets/expected-data.xml", excludeColumns = "id")
    public void test2() {
        manager.persist(person);
    }

    @Test
    @ExpectedDataSets(value = "datasets/expected-data.xml", excludeColumns = "id")
    public void test3() {
        manager.persist(person);

        // adding a new row to a table which is referenced by the expected data set but not included
        // in it will lead to a comparison error. Thus a AssertionError exception is expected
        manager.persist(new Person("Max", "Payne"));

        expectedException.expect(AssertionError.class);
    }

    @Test
    @ExpectedDataSets(value = "datasets/expected-data.xml", excludeColumns = "id")
    public void test4() {
        // adding a new row to a table which is not referenced by the expected data set will not
        // lead to a comparison error.
        person.addToFriends(new Person("Alex", "Balder"));

        manager.persist(new CookingRecipe("Muffin", "A tasty one"));
    }

    @Test
    @ExpectedDataSets(value = "datasets/expected-data.xml", excludeColumns = "id", strict = true)
    public void test5() {
        // adding a new row to a table which is not referenced by the expected data set will
        // lead to a comparison error in strict mode.
        manager.persist(new CookingRecipe("Muffin", "A tasty one"));

        manager.persist(person);

        expectedException.expect(AssertionError.class);
    }
}
