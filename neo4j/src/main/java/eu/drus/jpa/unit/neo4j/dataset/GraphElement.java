package eu.drus.jpa.unit.neo4j.dataset;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GraphElement {

    private String id;
    private List<Attribute> attributes;
    private List<String> labels;

    public GraphElement(final String id, final List<String> labels, final List<Attribute> attributes) {
        this.id = id;
        this.labels = labels;
        this.attributes = attributes;
    }

    public static String toType(final List<String> labels) {
        final List<String> tmp = labels.stream().sorted((a, b) -> a.compareTo(b)).collect(Collectors.toList());
        return String.join(":", tmp);
    }

    public List<String> getLabels() {
        return Collections.unmodifiableList(labels);
    }

    public String getType() {
        return toType(labels);
    }

    public String getId() {
        return id;
    }

    public List<Attribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }
}
