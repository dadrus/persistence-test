package eu.drus.jpa.unit.fixture.dbunit;

import java.util.List;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

@FunctionalInterface
public interface CleanupStrategyExecutor {
    void execute(final IDatabaseConnection connection, final List<IDataSet> initialDataSets, String... tablesToExclude)
            throws DbFeatureException;
}