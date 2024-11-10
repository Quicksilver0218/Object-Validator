package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class Contains extends Condition {
    private final String arg;

    public Contains(boolean reversed, String fieldExpression, String arg) {
        super(reversed, fieldExpression);
        this.arg = arg;
    }

    @Override
    protected boolean isFulfilledBy(Object field, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        if (field == null)
            throw new RuntimeException("Null values are not supported for 'contains'.");
        if (field instanceof String s)
            return s.contains(arg);
        if (field instanceof Object[] a) {
            for (Object o : a)
                if (o == null) {
                    if (arg == null)
                        return true;
                } else if (o.toString().equals(arg))
                    return true;
            return false;
        }
        if (field instanceof Iterable i) {
            for (Object o : i)
                if (o == null) {
                    if (arg == null)
                        return true;
                } else if (o.toString().equals(arg))
                    return true;
            return false;
        }
        throw new RuntimeException("Unsupported type for 'contains': " + field.getClass());
    }
}
