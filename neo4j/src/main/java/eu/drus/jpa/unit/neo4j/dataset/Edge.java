package eu.drus.jpa.unit.neo4j.dataset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Edge {

    private Node from;
    private Node to;
    private String id;
    private Map<String, ?> attributes;
    private List<String> references;

    public Edge(final Node from, final Node to, final String id, final Map<String, ?> attributes) {
        this(from, to, id, extractLabels(attributes), extractAttributes(attributes));
    }

    public Edge(final Node from, final Node to, final String id, final List<String> labels, final Map<String, ?> attributes) {
        this.from = from;
        this.to = to;
        this.id = id;
        references = labels;
        this.attributes = attributes;
    }

    private static Map<String, ?> extractAttributes(final Map<String, ?> attributes) {
        return attributes.entrySet().stream().filter(e -> !e.getKey().equals("label") && !e.getKey().equals("labels"))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private static List<String> extractLabels(final Map<String, ?> attributes) {
        return attributes.entrySet().stream().filter(e -> e.getKey().equals("label") || e.getKey().equals("labels"))
                .map(v -> v.getValue().toString().split(":")).flatMap(Arrays::stream).filter(v -> !v.isEmpty())
                .collect(Collectors.toList());
    }

    public List<String> getRelationships() {
        return Collections.unmodifiableList(references);
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Node getSourceNode() {
        return from;
    }

    public Node getTargetNode() {
        return to;
    }

    @Override
    public String toString() {
        return id;
    }
}
