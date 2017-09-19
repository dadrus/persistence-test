package eu.drus.jpa.unit.neo4j.operation;

import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.identifier;
import static org.neo4j.cypherdsl.CypherQuery.match;
import static org.neo4j.cypherdsl.CypherQuery.node;
import static org.neo4j.cypherdsl.CypherQuery.value;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.jgrapht.Graph;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.Identifier;
import org.neo4j.cypherdsl.Path;
import org.neo4j.cypherdsl.expression.ReferenceExpression;
import org.neo4j.cypherdsl.grammar.UpdateNext;

import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;

public class DeleteOperation implements Neo4JOperation {

    @Override
    public void execute(final Connection connection, final Graph<Node, Edge> graph) throws SQLException {
        final List<Path> nodes = graph.vertexSet().stream().map(DeleteOperation::toNodePath).collect(toList());
        final List<ReferenceExpression> nodeIds = graph.vertexSet().stream().map(n -> identifier(n.getId())).collect(toList());

        final List<Path> edges = graph.edgeSet().stream().map(DeleteOperation::toEdgePath).collect(toList());
        final List<ReferenceExpression> edgeIds = graph.edgeSet().stream().map(e -> identifier(e.getId())).collect(toList());

        final UpdateNext deleteEdgesQuery = match(edges.toArray(new Path[edges.size()])).delete(edgeIds);
        final UpdateNext deleteNodesQuery = match(nodes.toArray(new Path[nodes.size()])).delete(nodeIds);

        executeQuery(connection, deleteEdgesQuery);
        executeQuery(connection, deleteNodesQuery);
    }

    private void executeQuery(final Connection connection, final UpdateNext deleteEdgesQuery) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(deleteEdgesQuery.toString())) {
            ps.execute();
        }
    }

    private static Path toEdgePath(final Edge edge) {
        final List<Identifier> relationShips = edge.getRelationships().stream().map(CypherQuery::identifier).collect(toList());

        // @formatter:off
        return node(edge.getSourceNode().getId())
                .out(relationShips.toArray(new Identifier[relationShips.size()]))
                    .as(edge.getId())
                    .values(edge.getAttributes().entrySet().stream().map(e -> value(e.getKey(), e.getValue())).collect(toList()))
                .node(edge.getTargetNode().getId());
        // @formatter:on
    }

    private static Path toNodePath(final Node node) {
        // @formatter:off
        return node(node.getId())
                .labels(node.getLabels().stream().map(CypherQuery::label).collect(toList()))
                .values(node.getAttributes().entrySet().stream().map(e -> value(e.getKey(), e.getValue())).collect(toList()));
        // @formatter:on
    }
}
