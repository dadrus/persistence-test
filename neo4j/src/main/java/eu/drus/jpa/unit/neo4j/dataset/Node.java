package eu.drus.jpa.unit.neo4j.dataset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jgrapht.io.Attribute;

public class Node {

    private String id;
    private List<String> labels;
    private Map<String, ?> attributes;

    public Node(final String id, final Map<String, Attribute> attributes) {
        this.id = id;
        labels = attributes.entrySet().stream().filter(e -> e.getKey().equals("labels")).map(v -> v.getValue().getValue().split(":"))
                .flatMap(Arrays::stream).filter(v -> !v.isEmpty()).collect(Collectors.toList());

        this.attributes = attributes.entrySet().stream().filter(e -> !e.getKey().equals("labels"))
                .collect(Collectors.toMap(Entry::getKey, e -> AttributeTypeConverter.convert(e.getValue())));
    }

    public Node(final String id, final List<String> labels, final Map<String, ?> attributes) {
        this.id = id;
        this.labels = labels;
        this.attributes = attributes;
    }

    public List<String> getLabels() {
        return Collections.unmodifiableList(labels);
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public String toString() {
        return id;
    }

}
