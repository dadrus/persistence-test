package eu.drus.jpa.unit.neo4j.operation;

import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.identifier;
import static org.neo4j.cypherdsl.CypherQuery.literal;
import static org.neo4j.cypherdsl.CypherQuery.match;
import static org.neo4j.cypherdsl.CypherQuery.property;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.jgrapht.Graph;
import org.neo4j.cypherdsl.expression.SetExpression;
import org.neo4j.cypherdsl.grammar.UpdateNext;

import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;

public class UpdateOperation extends AbstractNeo4JOperation {

    @Override
    public void execute(final Connection connection, final Graph<Node, Edge> graph) throws SQLException {
        for (final Node node : graph.vertexSet()) {

            final List<SetExpression> attributes = node.getAttributes().entrySet().stream()
                    .map(e -> property(identifier(node.getId()).property(e.getKey()), literal(e.getValue()))).collect(toList());

            final UpdateNext query = match(node.toPath().withAttribute("id").build()).set(attributes);

            executeQuery(connection, query.toString());
        }
    }
}