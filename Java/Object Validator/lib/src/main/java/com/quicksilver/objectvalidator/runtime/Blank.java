package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class Blank extends Condition {
    public Blank(boolean reversed, String fieldExpression) {
        super(reversed, fieldExpression);
    }

    @Override
    protected boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        return switch (value) {
            case null -> throw new RuntimeException("Null values are not supported for 'blank'.");
            case String s -> s.isBlank();
            default -> throw new RuntimeException("Unsupported type for 'blank': " + value.getClass());
        };
    }
}
