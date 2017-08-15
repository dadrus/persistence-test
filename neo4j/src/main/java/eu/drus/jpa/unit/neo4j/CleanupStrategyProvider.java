package eu.drus.jpa.unit.neo4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import eu.drus.jpa.unit.api.CleanupStrategy.StrategyProvider;
import eu.drus.jpa.unit.neo4j.operation.Neo4JOperations;
import eu.drus.jpa.unit.spi.CleanupStrategyExecutor;
import eu.drus.jpa.unit.spi.DbFeatureException;

public class CleanupStrategyProvider implements StrategyProvider<CleanupStrategyExecutor<Connection, String>> {

    private static final String UNABLE_TO_CLEAN_DATABASE = "Unable to clean database.";

    @Override
    public CleanupStrategyExecutor<Connection, String> strictStrategy() {
        return (final Connection connection, final List<String> initialCollections, final String... collectionsToExclude) -> {
            try {
                Neo4JOperations.DELETE_ALL.execute(connection, null);
            } catch (final SQLException e) {
                throw new DbFeatureException(UNABLE_TO_CLEAN_DATABASE, e);
            }
        };
    }

    @Override
    public CleanupStrategyExecutor<Connection, String> usedTablesOnlyStrategy() {
        return (final Connection connection, final List<String> initialCollections, final String... collectionsToExclude) -> {
            // TODO
        };
    }

    @Override
    public CleanupStrategyExecutor<Connection, String> usedRowsOnlyStrategy() {
        return (final Connection connection, final List<String> initialCollections, final String... collectionsToExclude) -> {
            // TODO
        };
    }

}
