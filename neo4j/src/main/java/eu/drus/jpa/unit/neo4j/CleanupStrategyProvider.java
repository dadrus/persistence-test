package eu.drus.jpa.unit.neo4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;

import eu.drus.jpa.unit.api.CleanupStrategy.StrategyProvider;
import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;
import eu.drus.jpa.unit.neo4j.operation.Neo4JOperations;
import eu.drus.jpa.unit.spi.CleanupStrategyExecutor;
import eu.drus.jpa.unit.spi.DbFeatureException;

public class CleanupStrategyProvider implements StrategyProvider<CleanupStrategyExecutor<Connection, Graph<Node, Edge>>> {

    private static final String UNABLE_TO_CLEAN_DATABASE = "Unable to clean database.";

    @Override
    public CleanupStrategyExecutor<Connection, Graph<Node, Edge>> strictStrategy() {
        return (final Connection connection, final List<Graph<Node, Edge>> initialGraphs, final String... nodesToRetain) -> {

            try {
                Neo4JOperations.DELETE_ALL.execute(connection, readGraph(connection));
                connection.commit();
            } catch (final SQLException e) {
                throw new DbFeatureException(UNABLE_TO_CLEAN_DATABASE, e);
            }
        };
    }

    @Override
    public CleanupStrategyExecutor<Connection, Graph<Node, Edge>> usedTablesOnlyStrategy() {
        return (final Connection connection, final List<Graph<Node, Edge>> initialGraphs, final String... nodesToRetain) -> {
            if (initialGraphs.isEmpty()) {
                return;
            }

            try {
                for (final Graph<Node, Edge> graph : initialGraphs) {
                    Neo4JOperations.DELETE_ALL.execute(connection, computeGraphToBeDeleted(graph, nodesToRetain));
                }

                connection.commit();
            } catch (final SQLException e) {
                throw new DbFeatureException(UNABLE_TO_CLEAN_DATABASE, e);
            }
        };
    }

    @Override
    public CleanupStrategyExecutor<Connection, Graph<Node, Edge>> usedRowsOnlyStrategy() {
        return (final Connection connection, final List<Graph<Node, Edge>> initialGraphs, final String... nodesToRetain) -> {
            if (initialGraphs.isEmpty()) {
                return;
            }

            try {
                for (final Graph<Node, Edge> graph : initialGraphs) {
                    Neo4JOperations.DELETE.execute(connection, computeGraphToBeDeleted(graph, nodesToRetain));
                }

                connection.commit();
            } catch (final SQLException e) {
                throw new DbFeatureException(UNABLE_TO_CLEAN_DATABASE, e);
            }
        };
    }

    private Graph<Node, Edge> computeGraphToBeDeleted(final Graph<Node, Edge> graph, final String... nodesToRetain) {
        final Graph<Node, Edge> toDelete = new DefaultDirectedGraph<>(new ClassBasedEdgeFactory<>(Edge.class));

        // copy graph to a destination, which we are going to modify
        Graphs.addGraph(toDelete, graph);

        // remove the nodes, we have to retain from the graph
        Graphs.removeVerticesAndPreserveConnectivity(toDelete, v -> shouldRetainNode(v, nodesToRetain));

        return toDelete;
    }

    private boolean shouldRetainNode(final Node node, final String... nodesToRetain) {
        for (final String nodeToExclude : nodesToRetain) {
            if (node.getLabels().contains(nodeToExclude)) {
                return true;
            }
        }
        return false;
    }

    private Graph<Node, Edge> readGraph(final Connection connection) throws SQLException {
        final List<Node> nodes = new ArrayList<>();
        final List<Edge> edges = new ArrayList<>();

        readGraphElements(connection, edges, nodes);

        final DefaultDirectedGraph<Node, Edge> graph = new DefaultDirectedGraph<>(new ClassBasedEdgeFactory<>(Edge.class));
        nodes.forEach(graph::addVertex);
        edges.forEach(e -> graph.addEdge(e.getSourceNode(), e.getTargetNode(), e));

        return graph;
    }

    @SuppressWarnings("unchecked")
    private void readGraphElements(final Connection connection, final List<Edge> edgeList, final List<Node> nodeList) throws SQLException {
        final Map<Object, Map<String, ?>> edges = new HashMap<>();
        final Map<Object, Node> nodes = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(
                "MATCH (n) MATCH ()-[r]->() RETURN { id: id(n), labels: labels(n), attributes: properties(n) } as node, {id: id(r), label: type(r), attributes: properties(r), from: id(startNode(r)), to: id(endNode(r))} as relation")) {
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Map<String, ?> n = (Map<String, ?>) rs.getObject("node");
                    final Map<String, ?> r = (Map<String, ?>) rs.getObject("relation");
                    final Node node = toNode(n);
                    nodeList.add(node);
                    nodes.put(n.get("id"), node);
                    edges.put(r.get("id"), r);
                }
            }
        }

        for (final Map<String, ?> edge : edges.values()) {
            edgeList.add(toEdge(nodes, edge));
        }

    }

    @SuppressWarnings("unchecked")
    private static Node toNode(final Map<String, ?> node) {
        final Object id = node.get("id");
        final List<String> labels = (List<String>) node.get("labels");
        final Map<String, ?> attributes = (Map<String, ?>) node.get("attributes");
        return new Node(id.toString(), labels, attributes);
    }

    @SuppressWarnings("unchecked")
    private Edge toEdge(final Map<Object, Node> nodes, final Map<String, ?> edge) {
        final Node from = nodes.get(edge.get("from"));
        final Node to = nodes.get(edge.get("to"));

        return new Edge(from, to, edge.get("id").toString(), Arrays.asList(edge.get("label").toString()),
                (Map<String, ?>) edge.get("attributes"));
    }

}
