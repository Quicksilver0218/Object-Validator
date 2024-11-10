package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class Or extends Condition {
    protected final Condition[] conditions;

    public Or(boolean reversed, String fieldExpression, Condition... conditions) {
        super(reversed, fieldExpression);
        this.conditions = conditions;
    }

    @Override
    protected boolean isFulfilledBy(Object field, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) throws ReflectiveOperationException {
        HashSet<String> newFailedFields = new HashSet<>();
        for (Condition condition : conditions)
            if (condition.check(field, fullFieldExpression, passedFields, newFailedFields))
                return true;
        failedFields.addAll(newFailedFields);
        return false;
    }
}
