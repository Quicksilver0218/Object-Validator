package com.quicksilver.objectvalidator.runtime;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class Length extends Condition {
    private final String range;

    public Length(boolean reversed, String fieldExpression, String range) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    @Override
    protected boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        return switch (value) {
            case null -> throw new RuntimeException("Null values are not supported for 'length'.");
            case CharSequence s -> Utils.inRange(s.length(), range);
            case Object[] a -> Utils.inRange(a.length, range);
            case Collection<?> c -> Utils.inRange(c.size(), range);
            case Map<?, ?> m -> Utils.inRange(m.size(), range);
            default -> throw new RuntimeException("Unsupported type for 'length': " + value.getClass());
        };
    }
}