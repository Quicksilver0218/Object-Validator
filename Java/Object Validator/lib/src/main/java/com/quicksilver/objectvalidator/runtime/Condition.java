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

    private static void handleField(Object o, String field, List<Object> fields) throws ReflectiveOperationException {
        Class<?> clazz = o.getClass();
        ReflectiveOperationException e = null;
        do {
            try {
                fields.add(clazz.getDeclaredField(field).get(o));
                return;
            } catch (ReflectiveOperationException ex) {
                if (e == null)
                    e = ex;
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw e;
    }

    private static void handleIndex(Object list, int index, List<Object> fields) {
        if (list instanceof Object[] a)
            fields.add(a[index]);
        else
            fields.add(((List<?>)list).get(index));
    }

    private static void handleKey(Map<?, ?> map, String key, List<Object> fields) {
        fields.add(map.get(key));
    }

    private static void handleFields(List<Object> fields, String name, List<Object> newFields) throws ReflectiveOperationException {
        for (Object o : fields)
            if (o == null)
                newFields.add(null);
            else if (o instanceof Map m)
                handleKey(m, name, newFields);
            else {
                try {
                    if (o instanceof Object[] || o instanceof List)
                        handleIndex(o, Integer.parseInt(name), newFields);
                } catch (NumberFormatException unused) {}
                handleField(o, name, newFields);
            }
    }

    public boolean check(Object root, String rootFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) throws ReflectiveOperationException {
        List<Object> fields = new ArrayList<Object>() {{ add(root); }};
        if (fieldExpression != null) {
            if (root != null) {
                String fullName = "";
                for (String name : fieldExpression.split("\\.", -1)) {
                    ArrayList<Object> newFields = new ArrayList<>();
                    fullName += name;
                    if (fullName.equals("*"))
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
                    else if (name.length() >= 3 && name.substring(name.length() - 3, name.length() - 1).equals("//"))
                        fullName = fullName.substring(0, fullName.length() - 3) + fullName.substring(fullName.length() - 2);
                    else if (name.length() >= 2 && name.charAt(name.length() - 2) == '/') {
                        fullName = fullName.substring(0, fullName.length() - 2);
                        switch (Character.toUpperCase(name.charAt(name.length() - 1))) {
                            case 'C':
                                fullName += ".";
                                continue;
                            case 'F':
                                for (Object o : fields)
                                    if (o == null)
                                        newFields.add(null);
                                    else
                                        handleField(o, fullName, newFields);
                                break;
                            case 'I':
                                for (Object o : fields)
                                    if (o == null)
                                        newFields.add(null);
                                    else
                                        handleIndex(o, Integer.parseInt(fullName), newFields);
                                break;
                            case 'K':
                                for (Object o : fields)
                                    if (o == null)
                                        newFields.add(null);
                                    else
                                        handleKey((Map<?, ?>)o, fullName, newFields);
                                break;
                            case '*':
                                handleFields(fields, fullName + "*", newFields);
                                break;
                            default:
                                throw new RuntimeException("Unsupported suffix: " + name.charAt(name.length() - 1));
                        }
                    } else
                        handleFields(fields, fullName, newFields);
                    fields = newFields;
                    fullName = "";
                }
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
