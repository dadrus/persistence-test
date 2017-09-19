package eu.drus.jpa.unit.neo4j.graphml;

import java.util.Map;

/**
 * Creates a Vertex of type V
 *
 * @param <V>
 *            the vertex type
 */
public interface VertexProvider<V> {

    /**
     * Create a vertex
     *
     * @param label
     *            the label of the vertex
     * @param attributes
     *            any other attributes of the vertex
     *
     * @return the vertex
     */
    V buildVertex(String label, Map<String, Object> attributes);
}
