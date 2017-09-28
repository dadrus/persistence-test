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

import org.jgrapht.io.Attribute;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.Identifier;
import org.neo4j.cypherdsl.Path;
import org.neo4j.cypherdsl.PathRelationship;

public class Edge {

    private Node from;
    private Node to;
    private String id;
    private Map<String, ?> attributes;
    private List<String> labels;

    public Edge(final Node from, final Node to, final String id, final Map<String, Attribute> attributes) {
        this(from, to, id, extractLabels(attributes), extractAttributes(attributes));
    }

    public Edge(final Node from, final Node to, final String id, final List<String> labels, final Map<String, ?> attributes) {
        this.from = from;
        this.to = to;
        this.id = id;
        this.labels = labels;
        this.attributes = attributes;
    }

    private static Map<String, ?> extractAttributes(final Map<String, Attribute> attributes) {
        return attributes.entrySet().stream().filter(e -> !e.getKey().equals("label"))
                .collect(Collectors.toMap(Entry::getKey, e -> AttributeTypeConverter.convert(e.getValue())));
    }

    private static List<String> extractLabels(final Map<String, Attribute> attributes) {
        return attributes.entrySet().stream().filter(e -> e.getKey().equals("label")).map(v -> v.getValue().getValue().split(":"))
                .flatMap(Arrays::stream).filter(v -> !v.isEmpty()).sorted((a, b) -> a.compareTo(b)).collect(Collectors.toList());
    }

    public List<String> getRelationships() {
        return Collections.unmodifiableList(labels);
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

    public PathBuilder toPath() {
        return new PathBuilder();
    }

    @Override
    public String toString() {
        return id;
    }

    public class PathBuilder {

        private PathRelationship path;

        private PathBuilder() {
            final List<Identifier> relationShips = labels.stream().map(CypherQuery::identifier).collect(toList());
            path = node(from.getId()).out(relationShips.toArray(new Identifier[relationShips.size()])).as(id);
        }

        public PathBuilder withAttribute(final String attribute) {
            path = path.values(value(attribute, attributes.get(attribute)));
            return this;
        }

        public PathBuilder withAllAttributes() {
            path = path.values(attributes.entrySet().stream().map(e -> value(e.getKey(), e.getValue())).collect(toList()));
            return this;
        }

        public Path build() {
            return path.node(to.getId());
        }
    }
}
