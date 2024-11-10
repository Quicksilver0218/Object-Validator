package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class RegEx extends Condition {
    private final String pattern;

    public RegEx(boolean reversed, String fieldExpression, String pattern) {
        super(reversed, fieldExpression);
        this.pattern = pattern;
    }

    @Override
    protected boolean isFulfilledBy(Object field, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        if (field == null)
            throw new RuntimeException("Null values are not supported for 'regex'.");
        if (field instanceof String s)
            return s.matches(pattern);
        throw new RuntimeException("Unsupported type for 'regex': " + field.getClass());
    }
}
