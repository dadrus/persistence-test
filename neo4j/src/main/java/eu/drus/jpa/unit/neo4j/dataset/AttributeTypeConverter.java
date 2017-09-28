package eu.drus.jpa.unit.neo4j.dataset;

import org.jgrapht.io.Attribute;

import eu.drus.jpa.unit.api.JpaUnitException;

public final class AttributeTypeConverter {

    private AttributeTypeConverter() {}

    public static Object convert(final Attribute attribute) {
        switch (attribute.getType()) {
        case BOOLEAN:
            return Boolean.valueOf(attribute.getValue());
        case DOUBLE:
            return Double.valueOf(attribute.getValue());
        case FLOAT:
            // float types are always represented as double in neo4j
            // https://neo4j.com/docs/developer-manual/current/drivers/cypher-values/#driver-type-mappings
            return Double.valueOf(attribute.getValue());
        case INT:
            // int types are always represented as long in neo4j
            // https://neo4j.com/docs/developer-manual/current/drivers/cypher-values/#driver-type-mappings
            return Long.valueOf(attribute.getValue());
        case LONG:
            return Long.valueOf(attribute.getValue());
        case STRING:
            return attribute.getValue();
        default:
            throw new JpaUnitException("Unsupported attribute type: " + attribute.getType());
        }
    }

}
