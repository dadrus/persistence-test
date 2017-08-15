package eu.drus.jpa.unit.neo4j.operation;

import java.sql.Connection;
import java.sql.SQLException;

public interface Neo4JOperation {
    void execute(Connection connection, String data) throws SQLException;
}
