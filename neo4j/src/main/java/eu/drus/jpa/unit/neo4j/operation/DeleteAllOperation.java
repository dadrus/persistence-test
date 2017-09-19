package eu.drus.jpa.unit.neo4j.operation;

import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.match;
import static org.neo4j.cypherdsl.CypherQuery.node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jgrapht.Graph;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.grammar.StartNext;

import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;

public class DeleteAllOperation implements Neo4JOperation {

    @Override
    public void execute(final Connection connection, final Graph<Node, Edge> graph) throws SQLException {
        // MATCH (n) DETACH DELETE n

        for (final Node node : graph.vertexSet()) {
            final StartNext match = match(node("n").labels(node.getLabels().stream().map(CypherQuery::label).collect(toList())));
            final String deleteQuery = match.toString() + " DETACH DELETE n";

            try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
                ps.execute();
            }
        }

        // delete all indexes

    }

}
