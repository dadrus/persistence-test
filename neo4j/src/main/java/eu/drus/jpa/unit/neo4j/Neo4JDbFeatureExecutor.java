package eu.drus.jpa.unit.neo4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import eu.drus.jpa.unit.api.CleanupStrategy;
import eu.drus.jpa.unit.api.DataSeedStrategy;
import eu.drus.jpa.unit.api.ExpectedDataSets;
import eu.drus.jpa.unit.spi.AbstractDbFeatureExecutor;
import eu.drus.jpa.unit.spi.CleanupStrategyExecutor;
import eu.drus.jpa.unit.spi.DbFeature;
import eu.drus.jpa.unit.spi.DbFeatureException;
import eu.drus.jpa.unit.spi.FeatureResolver;

public class Neo4JDbFeatureExecutor extends AbstractDbFeatureExecutor<String, Connection> {

    protected Neo4JDbFeatureExecutor(final FeatureResolver featureResolver) {
        super(featureResolver);
    }

    @Override
    protected List<String> loadDataSets(final List<String> paths) {
        return Collections.emptyList();
    }

    @Override
    protected DbFeature<Connection> createCleanupFeature(final CleanupStrategy cleanupStrategy, final List<String> initialDataSets) {
        return (final Connection connection) -> {
            final CleanupStrategyExecutor<Connection, String> executor = cleanupStrategy.provide(new CleanupStrategyProvider());
            executor.execute(connection, initialDataSets);
        };
    }

    @Override
    protected DbFeature<Connection> createApplyCustomScriptFeature(final List<String> scriptPaths) {
        return (final Connection connection) -> {
            try {
                for (final String scriptPath : scriptPaths) {
                    executeScript(loadScript(scriptPath), connection);
                }
            } catch (IOException | URISyntaxException e) {
                throw new DbFeatureException("Could not apply custom scripts feature", e);
            }
        };
    }

    @Override
    protected DbFeature<Connection> createSeedDataFeature(final DataSeedStrategy dataSeedStrategy, final List<String> initialDataSets) {
        return (final Connection connection) -> {
            // TODO
        };
    }

    @Override
    protected DbFeature<Connection> createVerifyDataAfterFeature(final ExpectedDataSets expectedDataSets) {
        return (final Connection connection) -> {
            // TODO
        };
    }

    private void executeScript(final String script, final Connection connection) {
        // TODO Auto-generated method stub
    }

}
