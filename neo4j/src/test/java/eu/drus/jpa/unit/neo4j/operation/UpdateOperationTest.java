package eu.drus.jpa.unit.neo4j.operation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import eu.drus.jpa.unit.neo4j.dataset.Edge;
import eu.drus.jpa.unit.neo4j.dataset.Node;

@RunWith(MockitoJUnitRunner.class)
public class UpdateOperationTest {

    @Mock
    private Connection connection;

    @Spy
    private UpdateOperation operation;

    @Before
    public void prepareMocks() throws SQLException {
        doAnswer(i -> null).when(operation).executeQuery(any(Connection.class), anyString());
    }

    @Test
    public void testExecute() throws SQLException {
        // GIVEN
        final Graph<Node, Edge> graph = new DefaultDirectedGraph<>(new ClassBasedEdgeFactory<>(Edge.class));
        final Map<String, Object> n1Attrs = new HashMap<>();
        n1Attrs.put("id", new Long(1));
        final Map<String, Object> n2Attrs = new HashMap<>();
        n2Attrs.put("id", new Long(2));
        final Map<String, Object> e1Attrs = new HashMap<>();
        e1Attrs.put("id", new Long(3));
        final Node n1 = new Node("n1", Arrays.asList("Node"), n1Attrs);
        final Node n2 = new Node("n2", Arrays.asList("Node"), n2Attrs);
        final Edge e1 = new Edge(n1, n2, "e1", Arrays.asList("Edge"), e1Attrs);
        graph.addVertex(n1);
        graph.addVertex(n2);
        graph.addEdge(n1, n2, e1);

        // WHEN
        operation.execute(connection, graph);

        // THEN
        final ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(operation, times(2)).executeQuery(eq(connection), queryCaptor.capture());
        final List<String> queries = queryCaptor.getAllValues();
        final String query1 = queries.get(0);
        final String query2 = queries.get(1);
        assertThat(query1, containsString("MATCH (n1:Node {id:1}) SET n1.id=1"));
        assertThat(query2, containsString("MATCH (n2:Node {id:2}) SET n2.id=2"));
    }
}
