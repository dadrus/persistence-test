package eu.drus.jpa.unit.neo4j.dataset;

import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.node;
import static org.neo4j.cypherdsl.CypherQuery.value;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.Path;

public class Node extends GraphElement {

    Node(final String id, final List<String> labels, final List<Attribute> attributes) {
        super(id, labels, attributes);
    }

    public PathBuilder toPath() {
        return new PathBuilder();
    }

    @Override
    public String toString() {
        return getId();
    }

    public String asString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("id", getId());
        builder.append("labels", getLabels());
        builder.append("attributes", getAttributes());
        return builder.build();
    }

    public class PathBuilder {

        private Path path;

        private PathBuilder() {
            path = node(getId()).labels(getLabels().stream().map(CypherQuery::label).collect(toList()));
        }

        public PathBuilder withId(final String id) {
            path = node(id).labels(getLabels().stream().map(CypherQuery::label).collect(toList()));
            return this;
        }

        public PathBuilder withAttribute(final String attributeName) {
            path = path.values(value(attributeName, findAttribute(attributeName).getValue()));
            return this;
        }

        public PathBuilder withAllAttributes() {
            path = path.values(getAttributes().stream().map(a -> value(a.getName(), a.getValue())).collect(toList()));
            return this;
        }

        public PathBuilder withAllAttributesBut(final List<String> toExclude) {
            path = path.values(getAttributes().stream().filter(a -> !toExclude.contains(a.getName()))
                    .map(a -> value(a.getName(), a.getValue())).collect(toList()));
            return this;
        }

        public Path build() {
            return path;
        }

        private Attribute findAttribute(final String attributeName) {
            return getAttributes().stream().filter(a -> a.getName().equals(attributeName)).findFirst().orElse(null);
        }
    }
}
