package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class Contains extends Condition {
    private final String arg;

    public Contains(boolean reversed, String fieldExpression, String arg) {
        super(reversed, fieldExpression);
        this.arg = arg;
    }

    @Override
    protected boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        if (value == null)
            throw new RuntimeException("Null values are not supported for 'contains'.");
        if (value instanceof String s)
            return s.contains(arg);
        if (value instanceof Object[] a) {
            for (Object o : a)
                if (o == null) {
                    if (arg == null)
                        return true;
                } else if (o.toString().equals(arg))
                    return true;
            return false;
        }
        if (value instanceof Iterable i) {
            for (Object o : i)
                if (o == null) {
                    if (arg == null)
                        return true;
                } else if (o.toString().equals(arg))
                    return true;
            return false;
        }
        throw new RuntimeException("Unsupported type for 'contains': " + value.getClass());
    }
}
