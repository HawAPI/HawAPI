package com.lucasjosino.hawapi.enums.specification;

public enum SegmentationType {
    LIKE("*"),
    NOT_LIKE("!*"),
    BETWEEN("::"),
    NOT_IN("!:"),
    IN(":"),
    GREATER_OR_EQUALS_TO(">="),
    LESS_OR_EQUALS_TO("<="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    NOT_EQUALS("!"),
    EQUALS("");

    private final String value;

    SegmentationType(String value) {
        this.value = value;
    }

    public static SegmentationType get(String value) {
        // TODO: Check for false result.
        for (SegmentationType operator : SegmentationType.values()) {
            if (value.startsWith(operator.getValue()) || value.contains(operator.getValue())) return operator;
        }
        return SegmentationType.EQUALS;
    }

    public String getValue() {
        return value;
    }
}