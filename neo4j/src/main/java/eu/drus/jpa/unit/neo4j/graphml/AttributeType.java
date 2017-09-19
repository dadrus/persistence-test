package eu.drus.jpa.unit.neo4j.graphml;

public enum AttributeType {
    STRING("string") {
        @Override
        public Object convert(final String value) {
            return value;
        }
    },
    FLOAT("float") {
        @Override
        public Object convert(final String value) {
            return Float.valueOf(value);
        }
    },
    DOUBLE("double") {
        @Override
        public Object convert(final String value) {
            return Double.valueOf(value);
        }
    },
    LONG("long") {
        @Override
        public Object convert(final String value) {
            return Long.valueOf(value);
        }
    },
    BOOLEAN("boolean") {
        @Override
        public Object convert(final String value) {
            return Boolean.valueOf(value);
        }
    },
    INT("int") {
        @Override
        public Object convert(final String value) {
            return Integer.valueOf(value);
        }
    };

    private String type;

    private AttributeType(final String type) {
        this.type = type;
    }

    public abstract Object convert(String value);

    public static AttributeType fromString(final String name) {
        for (final AttributeType type : values()) {
            if (type.type.equals(name)) {
                return type;
            }
        }

        throw new IllegalArgumentException("No enum constant AttributeType." + name);
    }
}
