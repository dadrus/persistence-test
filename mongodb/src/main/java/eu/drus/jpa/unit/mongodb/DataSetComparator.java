package eu.drus.jpa.unit.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import eu.drus.jpa.unit.core.AssertionErrorCollector;
import eu.drus.jpa.unit.core.ColumnsHolder;

public class DataSetComparator {

    private ColumnsHolder toExclude;
    private ColumnsHolder orderBy;
    private boolean isStrict;

    public DataSetComparator(final String[] orderBy, final String[] toExclude, final boolean isStrict) {
        this.toExclude = new ColumnsHolder(toExclude);
        this.orderBy = new ColumnsHolder(orderBy);
        this.isStrict = isStrict;
    }

    public void compare(final MongoDatabase connection, final Document expectedDataSet, final AssertionErrorCollector errorCollector) {
        if (expectedDataSet.entrySet().isEmpty()) {
            shouldBeEmpty(connection, errorCollector);
        } else {
            compareContent(connection, expectedDataSet, errorCollector);
        }
    }

    private void shouldBeEmpty(final MongoDatabase connection, final AssertionErrorCollector errorCollector) {
        for (final String collectionName : connection.listCollectionNames()) {
            final long rowCount = connection.getCollection(collectionName).count();
            if (rowCount != 0) {
                errorCollector.collect(collectionName + " is expected to be empty, but has <" + rowCount + "> entries.");
            }
        }
    }

    private void compareContent(final MongoDatabase connection, final Document expectedDataSet,
            final AssertionErrorCollector errorCollector) {

        verifyCollectionNames(connection, expectedDataSet.keySet(), errorCollector);

        for (final String collectionName : expectedDataSet.keySet()) {
            verifyCollectionContent(connection, expectedDataSet, collectionName, errorCollector);
        }

        if (isStrict) {
            for (final String collectionName : connection.listCollectionNames()) {
                if (!expectedDataSet.keySet().contains(collectionName)) {
                    errorCollector.collect(collectionName + " is not expected, but present");
                }
            }
        }
    }

    private void verifyCollectionNames(final MongoDatabase connection, final Set<String> expectedCollectionNames,
            final AssertionErrorCollector errorCollector) {
        final List<String> currentCollections = new ArrayList<>();
        connection.listCollectionNames().iterator().forEachRemaining(currentCollections::add);
        for (final String expectedCollectionName : expectedCollectionNames) {
            if (!currentCollections.contains(expectedCollectionName)) {
                errorCollector.collect(expectedCollectionName + " is expected, but not present");
            }
        }
    }

    private void verifyCollectionContent(final MongoDatabase connection, final Document expectedDataSet, final String collectionName,
            final AssertionErrorCollector errorCollector) {
        final List<Document> expectedCollectionEntries = expectedDataSet.get(collectionName, List.class);
        final List<String> columnsToExclude = toExclude.getColumns(collectionName);
        final List<Document> foundEntries = new ArrayList<>();

        final MongoCollection<Document> currentCollection = connection.getCollection(collectionName);
        for (final Document expectedEntry : expectedCollectionEntries) {

            final FindIterable<Document> resultIt = currentCollection.find(filterRequest(expectedEntry, columnsToExclude));
            if (!resultIt.iterator().hasNext()) {
                errorCollector.collect(expectedEntry + " is expected in [" + collectionName + "], but not present");
            }

            resultIt.iterator().forEachRemaining(foundEntries::add);
        }

        final FindIterable<Document> allEntries = currentCollection.find();
        for (final Document d : allEntries) {
            if (!foundEntries.contains(d)) {
                errorCollector.collect(d + " is not expected in [" + collectionName + "], but present");
            }
        }
    }

    private Document filterRequest(final Document expectedEntry, final List<String> columnsToExclude) {
        final Document filtered = new Document();
        for (final Entry<String, Object> entry : expectedEntry.entrySet()) {
            if (!columnsToExclude.contains(entry.getKey())) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }
}