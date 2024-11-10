package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class True extends Condition {
    public True(boolean reversed, String fieldExpression) {
        super(reversed, fieldExpression);
    }

    @Override
    protected boolean isFulfilledBy(Object field, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        return field != null && (boolean)field;
    }
}
