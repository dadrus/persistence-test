package eu.drus.jpa.unit.neo4j.operation;

import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.identifier;
import static org.neo4j.cypherdsl.CypherQuery.literal;
import static org.neo4j.cypherdsl.CypherQuery.merge;
import static org.neo4j.cypherdsl.CypherQuery.property;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.jgrapht.Graph;
import org.neo4j.cypherdsl.expression.SetExpression;
import org.neo4j.cypherdsl.grammar.UpdateNext;

import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;

public class RefreshOperation extends AbstractNeo4JOperation {

    @Override
    public void execute(final Connection connection, final Graph<Node, Edge> graph) throws SQLException {
        for (final Node node : graph.vertexSet()) {

            final List<SetExpression> attributes = node.getAttributes().stream()
                    .map(a -> property(identifier(node.getId()).property(a.getName()), literal(a.getValue()))).collect(toList());

            final String identifierAttributeName = resolveIdentifierAttributeName(node);

            final UpdateNext query = merge(node.toPath().withAttribute(identifierAttributeName).build()).set(attributes);

            executeQuery(connection, query.toString());
        }
    }

    private String resolveIdentifierAttributeName(final Node node) {
        for (final String label : node.getLabels()) {
            // TODO: try to resolve the class name based on the label
            // for this we need the list of classes specified in the persistence.xml file
            // otherwise I see no possibility to scan for the given labels which might be
            // the actual simple class name or a name specified in @Table annotation.
            // If a corresponding class is found, the name of the property annotated with @Id or
            // value specified in @Column(name) has to be resolved.
        }
        // TODO Auto-generated method stub
        return "id";
    }
}
