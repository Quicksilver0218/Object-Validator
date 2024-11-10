package com.quicksilver.objectvalidator.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class Condition {
    protected final boolean reversed;
    protected final String fieldExpression;

    Condition(boolean reversed, String fieldExpression) {
        this.reversed = reversed;
        this.fieldExpression = fieldExpression;
    }

    public boolean check(Object root, String rootFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) throws ReflectiveOperationException {
        List<Object> fields = new ArrayList<Object>() {{ add(root); }};
        if (fieldExpression != null) {
            if (root != null)
                for (String name : fieldExpression.split("\\.", -1)) {
                    ArrayList<Object> newFields = new ArrayList<>();
                    if (name.equals("*"))
                        for (Object o : fields)
                            if (o == null)
                                newFields.add(null);
                            else if (o instanceof Object[] a)
                                for (Object item : a)
                                    newFields.add(item);
                            else if (o instanceof Iterable e)
                                for (Object item : e)
                                    newFields.add(item);
                            else
                                throw new RuntimeException("Unsupported type for iteration: " + o.getClass());
                    else {
                        boolean allAsterisk = true;
                        for (char c : name.toCharArray())
                            if (c != '*') {
                                allAsterisk = false;
                                break;
                            }
                        if (allAsterisk)
                            name = name.substring(1);
                        for (Object o : fields)
                            if (o == null)
                                newFields.add(null);
                            else if (o instanceof Map m)
                                newFields.add(m.get(name));
                            else {
                                try {
                                    if (o instanceof List l) {
                                        int index = Integer.parseInt(name);
                                        if (index < l.size())
                                            newFields.add(l.get(index));
                                        continue;
                                    }
                                } catch (NumberFormatException unused) {}
                                newFields.add(Utils.getFieldValue(o, name));
                            }
                    }
                    fields = newFields;
                }
            if (rootFieldExpression != null)
                rootFieldExpression += "." + fieldExpression;
            else
                rootFieldExpression = fieldExpression;
        }
        HashSet<String> childPassedFields = new HashSet<>(), childFailedFields = new HashSet<>();
        for (Object field : fields)
            if (isFulfilledBy(field, rootFieldExpression, childPassedFields, childFailedFields) == reversed) {
                if (rootFieldExpression != null)
                    failedFields.add(rootFieldExpression);
                if (reversed)
                    failedFields.addAll(childPassedFields);
                else
                    failedFields.addAll(childFailedFields);
                return false;
            }
        if (rootFieldExpression != null)
            passedFields.add(rootFieldExpression);
        if (reversed)
            passedFields.addAll(childFailedFields);
        else
            passedFields.addAll(childPassedFields);
        return true;
    }

    protected abstract boolean isFulfilledBy(Object field, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) throws ReflectiveOperationException;
}
