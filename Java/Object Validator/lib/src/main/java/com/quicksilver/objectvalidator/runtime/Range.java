package com.quicksilver.objectvalidator.runtime;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        if (!(value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof BigInteger || value instanceof Float || value instanceof Double || value instanceof BigDecimal || value instanceof Date))
            throw new RuntimeException("Unsupported type for 'range': " + value.getClass());
        return Utils.inRange(value, range);
    }
}
