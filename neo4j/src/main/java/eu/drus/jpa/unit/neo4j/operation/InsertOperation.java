package eu.drus.jpa.unit.neo4j.operation;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.create;
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
import org.neo4j.cypherdsl.grammar.UpdateNext;

import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;

public class InsertOperation implements Neo4JOperation {

    @Override
    public void execute(final Connection connection, final Graph<Node, Edge> graph) throws SQLException {
        // create query to insert nodes and edges
        final List<Path> paths = graph.vertexSet().stream().map(InsertOperation::toNodePath).collect(toList());
        graph.edgeSet().stream().map(InsertOperation::toEdgePath).collect(toCollection(() -> paths));

        final UpdateNext query = create(paths.toArray(new Path[paths.size()]));

        try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
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
