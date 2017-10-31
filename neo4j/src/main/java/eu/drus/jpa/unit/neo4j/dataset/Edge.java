package eu.drus.jpa.unit.neo4j.dataset;

import static java.util.stream.Collectors.toList;
import static org.neo4j.cypherdsl.CypherQuery.node;
import static org.neo4j.cypherdsl.CypherQuery.value;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.Identifier;
import org.neo4j.cypherdsl.Path;
import org.neo4j.cypherdsl.PathRelationship;

public class Edge extends GraphElement {

    private Node from;
    private Node to;

    Edge(final Node from, final Node to, final String id, final List<String> labels, final List<Attribute> attributes) {
        super(id, labels, attributes);
        this.from = from;
        this.to = to;
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
        return getId();
    }

    public boolean isSame(final Edge other, final List<String> attributesToExclude) {
        if (!super.isSame(other, attributesToExclude)) {
            return false;
        }

        return from.isSame(other.from, attributesToExclude) && to.isSame(other.to, attributesToExclude);
    }

    public String asString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("id", getId());
        builder.append("labels", getLabels());
        builder.append("from", from);
        builder.append("to", to);
        builder.append("attributes", getAttributes());
        return builder.build();
    }

    public class PathBuilder {

        private PathRelationship path;

        private PathBuilder() {
            final List<Identifier> relationShips = getLabels().stream().map(CypherQuery::identifier).collect(toList());
            path = node(from.getId()).out(relationShips.toArray(new Identifier[relationShips.size()])).as(getId());
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
            return path.node(to.getId());
        }

        private Attribute findAttribute(final String attributeName) {
            return getAttributes().stream().filter(a -> a.getName() == attributeName).findFirst().orElse(null);
        }
    }
}
