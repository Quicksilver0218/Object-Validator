package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class RegEx extends Condition {
    private final String pattern;

    public RegEx(boolean reversed, String fieldExpression, String pattern) {
        super(reversed, fieldExpression);
        this.pattern = pattern;
    }

    @Override
    protected boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        return switch (value) {
            case null -> throw new RuntimeException("Null values are not supported for 'regex'.");
            case String s -> s.matches(pattern);
            default -> throw new RuntimeException("Unsupported type for 'regex': " + value.getClass());
        };
    }
}
