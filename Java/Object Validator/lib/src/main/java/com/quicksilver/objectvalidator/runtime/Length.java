package com.quicksilver.objectvalidator.runtime;

import java.util.Collection;
import java.util.HashSet;

public class Length extends Condition {
    private final String range;

    public Length(boolean reversed, String fieldExpression, String range) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    @Override
    protected boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        if (value == null)
            throw new RuntimeException("Null values are not supported for 'length'.");
        if (value instanceof CharSequence s)
            return Utils.inRange(s.length(), range);
        if (value instanceof Object[] a)
            return Utils.inRange(a.length, range);
        if (value instanceof Collection c)
            return Utils.inRange(c.size(), range);
        throw new RuntimeException("Unsupported type for 'length': " + value.getClass());
    }
}