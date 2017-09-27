package eu.drus.jpa.unit.neo4j;

import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.identifier;
import static org.neo4j.cypherdsl.CypherQuery.match;
import static org.neo4j.cypherdsl.CypherQuery.node;
import static org.neo4j.cypherdsl.CypherQuery.value;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.Path;
import org.neo4j.cypherdsl.grammar.ReturnNext;

import eu.drus.jpa.unit.api.JpaUnitException;
import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;
import eu.drus.jpa.unit.spi.AssertionErrorCollector;
import eu.drus.jpa.unit.spi.ColumnsHolder;

public class GraphComparator {

    private static final String FAILED_TO_VERIFY_DATA_BASE_STATE = "Failed to verify data base state";

    private static final Function<String, String> ID_MAPPER = (final String name) -> name.equalsIgnoreCase("ID") ? "id" : name;

    private ColumnsHolder toExclude;
    private boolean isStrict;

    public GraphComparator(final String[] toExclude, final boolean strict) {
        this.toExclude = new ColumnsHolder(toExclude, ID_MAPPER);
        isStrict = strict;
    }

    public void compare(final Connection connection, final Graph<Node, Edge> expectedGraph, final AssertionErrorCollector errorCollector) {
        if (expectedGraph.vertexSet().isEmpty()) {
            shouldBeEmpty(connection, errorCollector);
        } else {
            compareContent(connection, expectedGraph, errorCollector);
        }
    }

    @SuppressWarnings("unchecked")
    private void shouldBeEmpty(final Connection connection, final AssertionErrorCollector errorCollector) {
        final Map<String, Integer> unexpectedNodesOccurence = new HashMap<>();

        try (final PreparedStatement ps = connection.prepareStatement("MATCH (n) RETURN { id: id(n), labels: labels(n) } as node")) {
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Map<String, ?> node = (Map<String, ?>) rs.getObject("node");
                    List<String> labels = (List<String>) node.get("labels");
                    labels = labels.stream().sorted((a, b) -> a.compareTo(b)).collect(Collectors.toList());
                    final String label = String.join(":", labels);
                    final Integer val = unexpectedNodesOccurence.computeIfPresent(label, (k, v) -> v + 1);
                    if (val == null) {
                        unexpectedNodesOccurence.put(label, 1);
                    }
                }
            }
        } catch (final SQLException e) {
            throw new JpaUnitException(FAILED_TO_VERIFY_DATA_BASE_STATE, e);
        }

        for (final Entry<String, Integer> nodeEntries : unexpectedNodesOccurence.entrySet()) {
            if (nodeEntries.getValue() != 0) {
                errorCollector.collect("No nodes with " + nodeEntries.getKey() + " labels were expected, but there are <"
                        + nodeEntries.getValue() + "> nodes present.");
            }
        }
    }

    private void compareContent(final Connection connection, final Graph<Node, Edge> expectedGraph,
            final AssertionErrorCollector errorCollector) {

        final List<String> expectedNodeLables = expectedGraph.vertexSet().stream().map(n -> String.join(":", n.getLabels()))
                .collect(Collectors.toList());

        final Set<String> currentNodeLabels = getAvailableNodeLabels(connection);

        verifyNodeLabels(currentNodeLabels, expectedNodeLables, errorCollector,
                "Nodes with %s labels were expected to be present, but not found");

        checkPresenceOfExpectedNodes(connection, expectedGraph, errorCollector);
        checkAbsenseOfNotExpectedNodes(connection, expectedGraph, errorCollector);

        if (isStrict) {
            verifyNodeLabels(expectedNodeLables, currentNodeLabels, errorCollector,
                    "Nodes with %s labels were not expected, but are present");
        }
    }

    @SuppressWarnings("unchecked")
    private void checkAbsenseOfNotExpectedNodes(final Connection connection, final Graph<Node, Edge> expectedGraph,
            final AssertionErrorCollector errorCollector) {
        final List<List<String>> expectedNodeLables = expectedGraph.vertexSet().stream().map(Node::getLabels).distinct()
                .collect(Collectors.toList());

        for (final List<String> labels : expectedNodeLables) {
            final List<Node> expectedNodes = expectedGraph.vertexSet().stream().filter(node -> node.getLabels().containsAll(labels))
                    .collect(Collectors.toList());

            final List<String> attributesToExclude = labels.stream().map(toExclude::getColumns).flatMap(List::stream).distinct()
                    .collect(Collectors.toList());

            final Path nodesPath = node("n").labels(labels.stream().map(CypherQuery::label).collect(toList()));
            final ReturnNext query = match(nodesPath).returns(identifier("n"));

            try (final PreparedStatement ps = connection.prepareStatement(query.toString())) {
                try (final ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final Map<String, Object> n = (Map<String, Object>) rs.getObject("n");
                        final boolean nodePresent = expectedNodes.stream().anyMatch(node -> {
                            final Set<Entry<String, Object>> attributes = node.getAttributes().entrySet().stream()
                                    .filter(e -> !attributesToExclude.contains(e.getKey())).collect(Collectors.toSet());

                            return n.entrySet().containsAll(attributes);
                        });
                        if (!nodePresent) {
                            errorCollector.collect("Node " + n + " was not expected, but is present");
                        }
                    }
                }
            } catch (final SQLException e) {
                throw new JpaUnitException(FAILED_TO_VERIFY_DATA_BASE_STATE, e);
            }
        }
    }

    private void checkPresenceOfExpectedNodes(final Connection connection, final Graph<Node, Edge> expectedGraph,
            final AssertionErrorCollector errorCollector) {
        for (final Node expectedNode : expectedGraph.vertexSet()) {
            final List<String> attributesToExclude = expectedNode.getLabels().stream().map(toExclude::getColumns).flatMap(List::stream)
                    .distinct().collect(Collectors.toList());

            final Path nodePath = toNodePath(expectedNode, attributesToExclude);
            final ReturnNext query = match(nodePath).returns(identifier(expectedNode.getId()));

            try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                if (!ps.execute()) {
                    errorCollector.collect("Node " + expectedNode + " was expected, but is not present");
                }
            } catch (final SQLException e) {
                throw new JpaUnitException(FAILED_TO_VERIFY_DATA_BASE_STATE, e);
            }
        }
    }

    private void verifyNodeLabels(final Collection<String> currentNodeLabels, final Collection<String> expectedNodeLabels,
            final AssertionErrorCollector errorCollector, final String formatString) {
        for (final String expectedNodeLabel : expectedNodeLabels) {
            if (!currentNodeLabels.contains(expectedNodeLabel)) {
                errorCollector.collect(String.format(formatString, expectedNodeLabel));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> getAvailableNodeLabels(final Connection connection) {
        final Set<String> currentNodeLabels = new HashSet<>();

        try (final PreparedStatement ps = connection.prepareStatement("MATCH (n) RETURN distinct labels(n) as labels")) {
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    List<String> labels = (List<String>) rs.getObject("labels");
                    labels = labels.stream().sorted((a, b) -> a.compareTo(b)).collect(Collectors.toList());
                    currentNodeLabels.add(String.join(":", labels));
                }
            }
        } catch (final SQLException e) {
            throw new JpaUnitException(FAILED_TO_VERIFY_DATA_BASE_STATE, e);
        }
        return currentNodeLabels;
    }

    private static Path toNodePath(final Node node, final List<String> attributesToExclude) {
        // @formatter:off
        return node(node.getId())
                .labels(node.getLabels().stream().map(CypherQuery::label).collect(toList()))
                .values(node.getAttributes().entrySet().stream()
                    .filter(e -> !attributesToExclude.contains(e.getKey()))
                    .map(e -> value(e.getKey(), e.getValue()))
                    .collect(toList()));
        // @formatter:on
    }
}
