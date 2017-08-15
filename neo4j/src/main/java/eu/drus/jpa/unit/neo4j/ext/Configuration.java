package eu.drus.jpa.unit.neo4j.ext;

import java.sql.Connection;
import java.sql.SQLException;

public interface Configuration {

    Connection getConnection() throws SQLException;
}
