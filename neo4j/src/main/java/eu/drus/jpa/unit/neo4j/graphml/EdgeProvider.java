package eu.drus.jpa.unit.neo4j.graphml;

import java.util.Map;

/**
 * Defines a provider of edges of type E
 *
 * @param <V>
 *            the type of vertex being linked.
 * @param <E>
 *            the type of edge being created.
 */
public interface EdgeProvider<V, E> {

    /**
     * Construct an edge
     *
     * @param from
     *            the source vertex
     * @param to
     *            the target vertex
     * @param label
     *            the label of the edge.
     * @param attributes
     *            extra attributes for the edge.
     *
     * @return the edge.
     */
    E buildEdge(V from, V to, String label, Map<String, Object> attributes);
}
