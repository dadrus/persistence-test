package eu.drus.jpa.unit.neo4j.operation;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class DeleteAllOperation implements Neo4JOperation {

    @Override
    public void execute(final Session session, final String data) {
        try (Transaction tx = session.beginTransaction()) {

            // deletes all nodes and relationships
            final StatementResult sr = tx.run(new Statement("MATCH (n) DETACH DELETE n"));
            sr.consume();

            // delete all indexes

            tx.success();
        }
    }

}
