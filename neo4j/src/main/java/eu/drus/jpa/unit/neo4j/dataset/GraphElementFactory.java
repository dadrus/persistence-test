package eu.drus.jpa.unit.neo4j.dataset;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.drus.jpa.unit.neo4j.graphml.Attribute;
import eu.drus.jpa.unit.neo4j.graphml.EdgeProvider;
import eu.drus.jpa.unit.neo4j.graphml.VertexProvider;

public class GraphElementFactory implements VertexProvider<Node>, EdgeProvider<Node, Edge> {

    private static Map<String, Object> convertAttributes(final Map<String, eu.drus.jpa.unit.neo4j.graphml.Attribute> attributes) {
        return attributes.entrySet().stream().filter(e -> !e.getKey().equals("labels") && !e.getKey().equals("label"))
                .collect(toMap(Entry::getKey, e -> AttributeTypeConverter.convert(e.getValue())));
    }

    private static List<String> extractLabels(final Map<String, eu.drus.jpa.unit.neo4j.graphml.Attribute> attributes) {
        return attributes.entrySet().stream().filter(e -> e.getKey().equals("labels") || e.getKey().equals("label"))
                .map(v -> v.getValue().getValue().split(":")).flatMap(Arrays::stream).filter(v -> !v.isEmpty()).sorted().collect(toList());
    }

    private List<eu.drus.jpa.unit.neo4j.dataset.Attribute> toAttributes(final Map<String, Object> propertiesMap) {
        return propertiesMap.entrySet().stream().map(e -> new eu.drus.jpa.unit.neo4j.dataset.Attribute(e.getKey(), e.getValue()))
                .collect(toList());
    }

    @Override
    public Edge buildEdge(final Node from, final Node to, final String name, final Map<String, Attribute> edgeAttributes) {
        return createEdge(from, to, name, extractLabels(edgeAttributes), convertAttributes(edgeAttributes));
    }

    public Edge createEdge(final Node from, final Node to, final String name, final List<String> labels,
            final Map<String, Object> attributesMap) {
        return new Edge(from, to, name, labels, toAttributes(attributesMap));
    }

    @Override
    public Node buildVertex(final String name, final Map<String, Attribute> nodeAttributes) {
        return createNode(name, extractLabels(nodeAttributes), convertAttributes(nodeAttributes));
    }

    public Node createNode(final String name, final List<String> labels, final Map<String, Object> attributesMap) {
        return new Node(name, labels, toAttributes(attributesMap));
    }
}
