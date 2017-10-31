package eu.drus.jpa.unit.neo4j.dataset;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jgrapht.Graph;
import org.junit.Test;

import eu.drus.jpa.unit.spi.DataSetLoader;
import eu.drus.jpa.unit.spi.UnsupportedDataSetFormatException;

public class DataSetLoaderProviderTest {

    private static final DataSetLoaderProvider LOADER_PROVIDER = new DataSetLoaderProvider(new GraphElementFactory());

    private static File getFile(final String path) throws URISyntaxException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        return new File(url.toURI());
    }

    @Test(expected = UnsupportedDataSetFormatException.class)
    public void testJsonLoaderNotSupported() {
        LOADER_PROVIDER.jsonLoader();
    }

    @Test(expected = UnsupportedDataSetFormatException.class)
    public void testYamlLoaderNotSupported() {
        LOADER_PROVIDER.yamlLoader();
    }

    @Test(expected = UnsupportedDataSetFormatException.class)
    public void testCsvLoaderNotSupported() {
        LOADER_PROVIDER.csvLoader();
    }

    @Test(expected = UnsupportedDataSetFormatException.class)
    public void testXlsLoaderNotSupported() {
        LOADER_PROVIDER.xlsLoader();
    }

    @Test
    public void testXmlLoaderLoadUsingProperResource() throws Exception {
        // WHEN
        final DataSetLoader<Graph<Node, Edge>> loader = LOADER_PROVIDER.xmlLoader();

        // THEN
        assertThat(loader, notNullValue());

        // WHEN
        final Graph<Node, Edge> graph = loader.load(getFile("test-data.xml"));

        // THEN
        assertThat(graph, notNullValue());

        // TODO: finalize me
    }

    @Test(expected = NullPointerException.class)
    public void testXmlLoaderLoadUsingNullFileName() throws IOException {
        // WHEN
        final DataSetLoader<Graph<Node, Edge>> loader = LOADER_PROVIDER.xmlLoader();

        // THEN
        assertThat(loader, notNullValue());

        // WHEN
        loader.load(null);

        // THEN
        // NullPointerException is thrown
    }

    @Test(expected = IOException.class)
    public void testXmlLoaderLoadUsingWrongResource() throws Exception {
        // WHEN
        final DataSetLoader<Graph<Node, Edge>> loader = LOADER_PROVIDER.xmlLoader();

        // THEN
        assertThat(loader, notNullValue());

        // WHEN
        loader.load(getFile("test-data.csv"));

        // THEN
        // Exception from the parser is thrown
    }
}
