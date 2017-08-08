package eu.drus.jpa.unit.neo4j.operation;

import org.neo4j.driver.v1.Session;

public interface Neo4JOperation {
    void execute(Session session, String data);
}
