package eu.drus.jpa.unit.neo4j.ext;

import org.neo4j.driver.v1.Driver;

public interface Configuration {

    Driver getDriver();
}
