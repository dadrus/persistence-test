package eu.drus.jpa.unit.neo4j.operation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.jgrapht.Graph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import eu.drus.jpa.unit.neo4j.dataset.Attribute;
import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;

@RunWith(MockitoJUnitRunner.class)
public class InsertOperationTest {

    @Mock
    private Connection connection;

    @Spy
    private InsertOperation operation;

    @Before
    public void prepareMocks() throws SQLException {
        doAnswer(i -> null).when(operation).executeQuery(any(Connection.class), anyString());
    }

    @Test
    public void testExecute() throws SQLException {
        // GIVEN
        final Node n1 = new Node("n1", Arrays.asList("Node"), Arrays.asList(new Attribute("id", 1l, true)));
        final Node n2 = new Node("n2", Arrays.asList("Node"), Arrays.asList(new Attribute("id", 2l, true)));
        final Edge e1 = new Edge(n1, n2, "e1", Arrays.asList("Edge"), Arrays.asList(new Attribute("id", 3l, true)));

        final Graph<Node, Edge> graph = new DefaultDirectedGraph<>(new ClassBasedEdgeFactory<>(Edge.class));
        graph.addVertex(n1);
        graph.addVertex(n2);
        graph.addEdge(n1, n2, e1);

        // WHEN
        operation.execute(connection, graph);

        // THEN
        final ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(operation).executeQuery(eq(connection), queryCaptor.capture());
        final String query = queryCaptor.getValue();
        assertThat(query, containsString("CREATE (n1:Node {id:1}),(n2:Node {id:2}),(n1)-[e1:Edge {id:3}]->(n2)"));
    }
}
