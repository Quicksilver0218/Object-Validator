package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class And extends Condition {
    protected final Condition[] conditions;

    public And(boolean reversed, String fieldExpression, Condition... conditions) {
        super(reversed, fieldExpression);
        this.conditions = conditions;
    }

    @Override
    protected boolean isFulfilledBy(Object field, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) throws ReflectiveOperationException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        HashSet<String> newPassedFields = new HashSet<>();
        for (Condition condition : conditions)
            if (!condition.check(field, fullFieldExpression, newPassedFields, failedFields))
                return false;
        passedFields.addAll(newPassedFields);
        return true;
    }
}
