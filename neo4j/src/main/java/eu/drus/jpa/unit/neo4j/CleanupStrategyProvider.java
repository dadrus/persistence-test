package eu.drus.jpa.unit.neo4j;

import java.util.List;

import org.neo4j.driver.v1.Session;

import eu.drus.jpa.unit.api.CleanupStrategy.StrategyProvider;
import eu.drus.jpa.unit.neo4j.operation.Neo4JOperations;
import eu.drus.jpa.unit.spi.CleanupStrategyExecutor;

public class CleanupStrategyProvider implements StrategyProvider<CleanupStrategyExecutor<Session, String>> {

    @Override
    public CleanupStrategyExecutor<Session, String> strictStrategy() {
        return (final Session connection, final List<String> initialCollections, final String... collectionsToExclude) -> {
            Neo4JOperations.DELETE_ALL.execute(connection, null);
        };
    }

    @Override
    public CleanupStrategyExecutor<Session, String> usedTablesOnlyStrategy() {
        return (final Session connection, final List<String> initialCollections, final String... collectionsToExclude) -> {
            // TODO
        };
    }

    @Override
    public CleanupStrategyExecutor<Session, String> usedRowsOnlyStrategy() {
        return (final Session connection, final List<String> initialCollections, final String... collectionsToExclude) -> {
            // TODO
        };
    }

}
