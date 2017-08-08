package eu.drus.jpa.unit.neo4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.neo4j.driver.v1.Session;

import eu.drus.jpa.unit.api.CleanupStrategy;
import eu.drus.jpa.unit.api.DataSeedStrategy;
import eu.drus.jpa.unit.api.ExpectedDataSets;
import eu.drus.jpa.unit.spi.AbstractDbFeatureExecutor;
import eu.drus.jpa.unit.spi.CleanupStrategyExecutor;
import eu.drus.jpa.unit.spi.DbFeature;
import eu.drus.jpa.unit.spi.DbFeatureException;
import eu.drus.jpa.unit.spi.FeatureResolver;

public class Neo4JDbFeatureExecutor extends AbstractDbFeatureExecutor<String, Session> {

    protected Neo4JDbFeatureExecutor(final FeatureResolver featureResolver) {
        super(featureResolver);
    }

    @Override
    protected List<String> loadDataSets(final List<String> paths) {
        return Collections.emptyList();
    }

    @Override
    protected DbFeature<Session> createCleanupFeature(final CleanupStrategy cleanupStrategy, final List<String> initialDataSets) {
        return (final Session connection) -> {
            final CleanupStrategyExecutor<Session, String> executor = cleanupStrategy.provide(new CleanupStrategyProvider());
            executor.execute(connection, initialDataSets);
        };
    }

    @Override
    protected DbFeature<Session> createApplyCustomScriptFeature(final List<String> scriptPaths) {
        return (final Session connection) -> {
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
    protected DbFeature<Session> createSeedDataFeature(final DataSeedStrategy dataSeedStrategy, final List<String> initialDataSets) {
        return (final Session connection) -> {
            // TODO
        };
    }

    @Override
    protected DbFeature<Session> createVerifyDataAfterFeature(final ExpectedDataSets expectedDataSets) {
        return (final Session connection) -> {
            // TODO
        };
    }

    private void executeScript(final String script, final Session connection) {
        // TODO Auto-generated method stub
    }

}
