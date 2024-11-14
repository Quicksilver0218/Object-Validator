package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class Null extends Condition {
    public Null(boolean reversed, String fieldExpression) {
        super(reversed, fieldExpression);
    }

    @Override
    protected boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        return value == null;
    }
}
