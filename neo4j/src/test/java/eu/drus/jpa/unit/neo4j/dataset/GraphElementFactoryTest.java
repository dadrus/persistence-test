package eu.drus.jpa.unit.neo4j.dataset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import eu.drus.jpa.unit.neo4j.graphml.AttributeType;
import eu.drus.jpa.unit.neo4j.graphml.DefaultAttribute;

public class GraphElementFactoryTest {

    private GraphElementFactory factory = new GraphElementFactory();

    @Test
    public void testCreateNode() {
        // GIVEN
        final String nodeId = "a";
        final List<String> nodeLabels = Arrays.asList("A");
        final Map<String, Object> nodeAttributes = ImmutableMap.<String, Object>builder().put("id", 1l).put("value", "foo").build();

        // WHEN
        final Node node = factory.createNode(nodeId, nodeLabels, nodeAttributes);

        // THEN
        assertThat(node, notNullValue());
        assertThat(node.getId(), equalTo(nodeId));
        assertThat(node.getLabels(), equalTo(nodeLabels));
        assertThat(node.getAttributes().size(), equalTo(2));

        final Attribute idAttribute = node.getAttributes().get(0);
        assertThat(idAttribute.getName(), equalTo("id"));
        assertThat(idAttribute.getValue(), equalTo(1l));

        final Attribute valueAttribute = node.getAttributes().get(1);
        assertThat(valueAttribute.getName(), equalTo("value"));
        assertThat(valueAttribute.getValue(), equalTo("foo"));
    }

    @Test
    public void testBuildVertex() {
        // GIVEN
        final String nodeId = "a";
        final Map<String, eu.drus.jpa.unit.neo4j.graphml.Attribute> nodeAttributes = ImmutableMap.<String, eu.drus.jpa.unit.neo4j.graphml.Attribute>builder()
                .put("id", new DefaultAttribute<>(1L, AttributeType.LONG)).put("value", new DefaultAttribute<>("foo", AttributeType.STRING))
                .put("labels", new DefaultAttribute<>("A", AttributeType.STRING)).build();

        // WHEN
        final Node node = factory.buildVertex(nodeId, nodeAttributes);

        // THEN
        assertThat(node, notNullValue());
        assertThat(node.getId(), equalTo(nodeId));
        assertThat(node.getLabels(), equalTo(Arrays.asList("A")));
        assertThat(node.getAttributes().size(), equalTo(2));

        final Attribute idAttribute = node.getAttributes().get(0);
        assertThat(idAttribute.getName(), equalTo("id"));
        assertThat(idAttribute.getValue(), equalTo(1l));

        final Attribute valueAttribute = node.getAttributes().get(1);
        assertThat(valueAttribute.getName(), equalTo("value"));
        assertThat(valueAttribute.getValue(), equalTo("foo"));
    }

    @Test
    public void testCreateEdge() {
        // GIVEN
        final String nodeAId = "a";
        final List<String> nodeALabels = Arrays.asList("A");
        final Map<String, Object> nodeAAttributes = ImmutableMap.<String, Object>builder().put("id", 1l).put("value", "foo").build();

        final String nodeBId = "b";
        final List<String> nodeBLabels = Arrays.asList("A");
        final Map<String, Object> nodeBAttributes = ImmutableMap.<String, Object>builder().put("id", 2l).put("value", "boo").build();

        final String edgeId = "e";
        final List<String> edgeLabels = Arrays.asList("edge");
        final Map<String, Object> edgeAttributes = ImmutableMap.<String, Object>builder().put("value", "moo").build();

        final Node nodeA = factory.createNode(nodeAId, nodeALabels, nodeAAttributes);
        final Node nodeB = factory.createNode(nodeBId, nodeBLabels, nodeBAttributes);

        // WHEN
        final Edge edge = factory.createEdge(nodeA, nodeB, edgeId, edgeLabels, edgeAttributes);

        // THEN
        assertThat(edge, notNullValue());
        assertThat(edge.getId(), equalTo(edgeId));
        assertThat(edge.getLabels(), equalTo(edgeLabels));
        assertThat(edge.getAttributes().size(), equalTo(1));
        assertThat(edge.getSourceNode(), equalTo(nodeA));
        assertThat(edge.getTargetNode(), equalTo(nodeB));

        final Attribute idAttribute = edge.getAttributes().get(0);
        assertThat(idAttribute.getName(), equalTo("value"));
        assertThat(idAttribute.getValue(), equalTo("moo"));
    }

    @Test
    public void testBuildEdge() {
        // GIVEN
        final String nodeAId = "a";
        final List<String> nodeALabels = Arrays.asList("A");
        final Map<String, Object> nodeAAttributes = ImmutableMap.<String, Object>builder().put("id", 1l).put("value", "foo").build();

        final String nodeBId = "b";
        final List<String> nodeBLabels = Arrays.asList("A");
        final Map<String, Object> nodeBAttributes = ImmutableMap.<String, Object>builder().put("id", 2l).put("value", "boo").build();

        final String edgeId = "e";
        final Map<String, eu.drus.jpa.unit.neo4j.graphml.Attribute> edgeAttributes = ImmutableMap.<String, eu.drus.jpa.unit.neo4j.graphml.Attribute>builder()
                .put("value", new DefaultAttribute<>("moo", AttributeType.STRING))
                .put("label", new DefaultAttribute<>("edge", AttributeType.STRING)).build();

        final Node nodeA = factory.createNode(nodeAId, nodeALabels, nodeAAttributes);
        final Node nodeB = factory.createNode(nodeBId, nodeBLabels, nodeBAttributes);

        // WHEN
        final Edge edge = factory.buildEdge(nodeA, nodeB, edgeId, edgeAttributes);

        // THEN
        assertThat(edge, notNullValue());
        assertThat(edge.getId(), equalTo(edgeId));
        assertThat(edge.getLabels(), equalTo(Arrays.asList("edge")));
        assertThat(edge.getAttributes().size(), equalTo(1));
        assertThat(edge.getSourceNode(), equalTo(nodeA));
        assertThat(edge.getTargetNode(), equalTo(nodeB));

        final Attribute idAttribute = edge.getAttributes().get(0);
        assertThat(idAttribute.getName(), equalTo("value"));
        assertThat(idAttribute.getValue(), equalTo("moo"));
    }
}
