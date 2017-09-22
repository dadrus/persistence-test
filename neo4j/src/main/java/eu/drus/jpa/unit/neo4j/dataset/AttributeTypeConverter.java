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
            return Float.valueOf(attribute.getValue());
        case INT:
            return Integer.valueOf(attribute.getValue());
        case LONG:
            return Long.valueOf(attribute.getValue());
        case STRING:
            return attribute.getValue();
        default:
            throw new JpaUnitException("Unsupported attribute type: " + attribute.getType());
        }
    }

}
