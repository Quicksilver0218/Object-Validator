package com.quicksilver.objectvalidator.runtime;

import java.util.Date;
import java.util.HashSet;

public class Range extends Condition {
    private final String range;

    public Range(boolean reversed, String fieldExpression, String range) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    @Override
    protected boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        if (value == null)
            throw new RuntimeException("Null values are not supported for 'range'.");
        if (value instanceof Number || value instanceof Date)
            return Utils.inRange(value, range);
        throw new RuntimeException("Unsupported type for 'range': " + value.getClass());
    }
}
