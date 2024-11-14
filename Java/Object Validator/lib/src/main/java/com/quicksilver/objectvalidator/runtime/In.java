package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class In extends Condition {
    private final String[] args;

    public In(boolean reversed, String fieldExpression, String... args) {
        super(reversed, fieldExpression);
        this.args = args;
    }

    @Override
    protected boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        if (value == null) {
            for (String arg : args)
                if (arg == null)
                    return true;
            return false;
        }
        for (String arg : args)
            if (value.toString().equals(arg))
                return true;
        return false;
    }
}
