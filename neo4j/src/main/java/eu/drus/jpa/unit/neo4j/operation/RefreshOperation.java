package eu.drus.jpa.unit.neo4j.operation;

import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.identifier;
import static org.neo4j.cypherdsl.CypherQuery.literal;
import static org.neo4j.cypherdsl.CypherQuery.merge;
import static org.neo4j.cypherdsl.CypherQuery.node;
import static org.neo4j.cypherdsl.CypherQuery.property;
import static org.neo4j.cypherdsl.CypherQuery.value;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.jgrapht.Graph;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.Path;
import org.neo4j.cypherdsl.expression.SetExpression;
import org.neo4j.cypherdsl.grammar.UpdateNext;

import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;

public class RefreshOperation implements Neo4JOperation {

    @Override
    public void execute(final Connection connection, final Graph<Node, Edge> graph) throws SQLException {
        for (final Node node : graph.vertexSet()) {

            final List<SetExpression> attributes = node.getAttributes().entrySet().stream()
                    .map(e -> property(identifier(node.getId()).property(e.getKey()), literal(e.getValue()))).collect(toList());

            final UpdateNext setQuery = merge(toNodePath(node)).set(attributes);

            try (PreparedStatement ps = connection.prepareStatement(setQuery.toString())) {
                ps.execute();
            }
        }
    }

    private static Path toNodePath(final Node node) {
        final Object id = node.getAttributes().get("id");
        // @formatter:off
        return node(node.getId())
                .labels(node.getLabels().stream().map(CypherQuery::label).collect(toList()))
                .values(value("id", id));
        // @formatter:on
    }

}
