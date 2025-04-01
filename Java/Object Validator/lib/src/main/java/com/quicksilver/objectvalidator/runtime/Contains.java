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
        return switch (value) {
            case null -> throw new RuntimeException("Null values are not supported for 'contains'.");
            case String s -> s.contains(arg);
            case Object[] a -> {
                for (Object o : a)
                    if (o == null) {
                        if (arg == null)
                            yield true;
                    } else if (o.toString().equals(arg))
                        yield true;
                yield false;
            }
            case Iterable<?> i -> {
                for (Object o : i)
                    if (o == null) {
                        if (arg == null)
                            yield true;
                    } else if (o.toString().equals(arg))
                        yield true;
                yield false;
            }
            default -> throw new RuntimeException("Unsupported type for 'contains': " + value.getClass());
        };
    }
}
