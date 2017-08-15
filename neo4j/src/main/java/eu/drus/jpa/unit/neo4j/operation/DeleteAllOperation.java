package eu.drus.jpa.unit.neo4j.operation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllOperation implements Neo4JOperation {

    @Override
    public void execute(final Connection connection, final String data) throws SQLException {

        // deletes all nodes and relationships
        try (PreparedStatement ps = connection.prepareStatement("MATCH (n) DETACH DELETE n")) {
            ps.execute();
        }

        // delete all indexes

        connection.commit();
    }

}
