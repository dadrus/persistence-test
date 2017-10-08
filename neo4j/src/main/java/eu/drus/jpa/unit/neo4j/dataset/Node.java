package eu.drus.jpa.unit.neo4j.dataset;

import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.node;
import static org.neo4j.cypherdsl.CypherQuery.value;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.Path;

import eu.drus.jpa.unit.neo4j.graphml.Attribute;

public class Node {

    private String id;
    private List<String> labels;
    private Map<String, ?> attributes;

    public Node(final String id, final Map<String, Attribute> attributes) {
        this(id, extractLabels(attributes), extractAttributes(attributes));
    }

    public Node(final String id, final List<String> labels, final Map<String, ?> attributes) {
        this.id = id;
        this.labels = labels;
        this.attributes = attributes;
    }

    private static Map<String, Object> extractAttributes(final Map<String, Attribute> attributes) {
        return attributes.entrySet().stream().filter(e -> !e.getKey().equals("labels"))
                .collect(Collectors.toMap(Entry::getKey, e -> AttributeTypeConverter.convert(e.getValue())));
    }

    private static List<String> extractLabels(final Map<String, Attribute> attributes) {
        return attributes.entrySet().stream().filter(e -> e.getKey().equals("labels")).map(v -> v.getValue().getValue().split(":"))
                .flatMap(Arrays::stream).filter(v -> !v.isEmpty()).collect(Collectors.toList());
    }

    public static String toNodeType(final List<String> labels) {
        final List<String> tmp = labels.stream().sorted((a, b) -> a.compareTo(b)).collect(Collectors.toList());
        return String.join(":", tmp);
    }

    public List<String> getLabels() {
        return Collections.unmodifiableList(labels);
    }

    public String getType() {
        return toNodeType(labels);
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public PathBuilder toPath() {
        return new PathBuilder();
    }

    @Override
    public String toString() {
        return id;
    }

    public class PathBuilder {

        private Path path;

        private PathBuilder() {
            path = node(id).labels(labels.stream().map(CypherQuery::label).collect(toList()));
        }

        public PathBuilder withId(final String id) {
            path = node(id).labels(labels.stream().map(CypherQuery::label).collect(toList()));
            return this;
        }

        public PathBuilder withAttribute(final String attribute) {
            path = path.values(value(attribute, attributes.get(attribute)));
            return this;
        }

        public PathBuilder withAllAttributes() {
            path = path.values(attributes.entrySet().stream().map(e -> value(e.getKey(), e.getValue())).collect(toList()));
            return this;
        }

        public PathBuilder withAllAttributesBut(final List<String> toExclude) {
            path = path.values(attributes.entrySet().stream().filter(e -> !toExclude.contains(e.getKey()))
                    .map(e -> value(e.getKey(), e.getValue())).collect(toList()));
            return this;
        }

        public Path build() {
            return path;
        }
    }
}
