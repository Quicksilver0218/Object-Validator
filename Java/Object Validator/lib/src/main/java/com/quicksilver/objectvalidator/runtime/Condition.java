package com.quicksilver.objectvalidator.runtime;

import java.lang.reflect.Field;
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

    private static void handleField(Object obj, String fieldName, List<Object> values) throws ReflectiveOperationException {
        Class<?> clazz = obj.getClass();
        ReflectiveOperationException e = null;
        do {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                values.add(field.get(obj));
                return;
            } catch (ReflectiveOperationException ex) {
                if (e == null)
                    e = ex;
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw e;
    }

    private static void handleIndex(Object list, int index, List<Object> values) {
        if (list instanceof Object[] a)
            values.add(a[index]);
        else
            values.add(((List<?>)list).get(index));
    }

    private static void handleKey(Map<?, ?> map, String key, List<Object> values) {
        values.add(map.get(key));
    }

    private static void handleValues(List<Object> values, String name, List<Object> newValues) throws ReflectiveOperationException {
        for (Object value : values)
            if (value == null)
                newValues.add(null);
            else if (value instanceof Map m)
                handleKey(m, name, newValues);
            else {
                try {
                    if (value instanceof Object[] || value instanceof List)
                        handleIndex(value, Integer.parseInt(name), newValues);
                } catch (NumberFormatException unused) {}
                handleField(value, name, newValues);
            }
    }

    public boolean check(Object root, String rootFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) throws ReflectiveOperationException {
        List<Object> values = new ArrayList<Object>() {{ add(root); }};
        if (fieldExpression != null) {
            if (root != null) {
                String fullName = "";
                for (String name : fieldExpression.split("\\.", -1)) {
                    ArrayList<Object> newValues = new ArrayList<>();
                    fullName += name;
                    if (fullName.equals("*"))
                        for (Object o : values)
                            if (o == null)
                                newValues.add(null);
                            else if (o instanceof Object[] a)
                                for (Object item : a)
                                    newValues.add(item);
                            else if (o instanceof Iterable e)
                                for (Object item : e)
                                    newValues.add(item);
                            else
                                throw new RuntimeException("Unsupported type for iteration: " + o.getClass());
                    else if (name.length() >= 3 && name.substring(name.length() - 3, name.length() - 1).equals("//"))
                        fullName = fullName.substring(0, fullName.length() - 3) + fullName.substring(fullName.length() - 2);
                    else if (name.length() >= 2 && name.charAt(name.length() - 2) == '/') {
                        fullName = fullName.substring(0, fullName.length() - 2);
                        switch (Character.toUpperCase(name.charAt(name.length() - 1))) {
                            case 'C':
                                fullName += ".";
                                continue;
                            case 'F':
                                for (Object o : values)
                                    if (o == null)
                                        newValues.add(null);
                                    else
                                        handleField(o, fullName, newValues);
                                break;
                            case 'I':
                                for (Object o : values)
                                    if (o == null)
                                        newValues.add(null);
                                    else
                                        handleIndex(o, Integer.parseInt(fullName), newValues);
                                break;
                            case 'K':
                                for (Object o : values)
                                    if (o == null)
                                        newValues.add(null);
                                    else
                                        handleKey((Map<?, ?>)o, fullName, newValues);
                                break;
                            case '*':
                                handleValues(values, fullName + "*", newValues);
                                break;
                            default:
                                throw new RuntimeException("Unsupported suffix: " + name.charAt(name.length() - 1));
                        }
                    } else
                        handleValues(values, fullName, newValues);
                    values = newValues;
                    fullName = "";
                }
            }
            if (rootFieldExpression != null)
                rootFieldExpression += "." + fieldExpression;
            else
                rootFieldExpression = fieldExpression;
        }
        HashSet<String> childPassedFields = new HashSet<>(), childFailedFields = new HashSet<>();
        for (Object value : values)
            if (isFulfilledBy(value, rootFieldExpression, childPassedFields, childFailedFields) == reversed) {
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

    protected abstract boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) throws ReflectiveOperationException;
}
