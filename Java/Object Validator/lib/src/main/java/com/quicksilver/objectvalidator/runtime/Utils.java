package com.quicksilver.objectvalidator.runtime;

import java.util.Arrays;

class Utils {
    static Object getFieldValue(Object o, String name) throws ReflectiveOperationException {
        Class<?> clazz = o.getClass();
        ReflectiveOperationException e = null;
        do {
            try {
                return clazz.getDeclaredField(name).get(o);
            } catch (ReflectiveOperationException ex) {
                if (e == null)
                    e = ex;
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw e;
    }

    static boolean inRange(long num, String range) {
        for (String s : range.split(",")) {
            String limit = s.trim();
            if (limit.startsWith("[")) {
                if (num < Long.parseLong(limit.substring(1)))
                    return false;
            } else if (limit.startsWith("(")) {
                if (num <= Long.parseLong(limit.substring(1)))
                    return false;
            } else if (limit.endsWith("]")) {
                if (num > Long.parseLong(limit.substring(0, limit.length() - 1)))
                    return false;
            } else if (limit.endsWith(")")) {
                if (num >= Long.parseLong(limit.substring(0, limit.length() - 1)))
                    return false;
            } else if (num != Long.parseLong(limit))
                return false;
        }
        return true;
    }

    static Condition buildRuntimeCondition(com.quicksilver.objectvalidator.config.Condition condition) {
        String t = condition.type.trim().toLowerCase();
        boolean reversed = false;
        while (t.startsWith("!")) {
            reversed = !reversed;
            t = t.substring(1).trim();
        }
        return switch (t) {
            case "and" -> new And(reversed, condition.field, Arrays.stream(condition.conditions).map(Utils::buildRuntimeCondition).toArray(Condition[]::new));
            case "or" -> new Or(reversed, condition.field, Arrays.stream(condition.conditions).map(Utils::buildRuntimeCondition).toArray(Condition[]::new));
            case "null" -> new Null(reversed, condition.field);
            case "in" -> new In(reversed, condition.field, condition.args);
            case "blank" -> new Blank(reversed, condition.field);
            case "regex" -> new RegEx(reversed, condition.field, condition.arg);
            case "bytes" -> new Bytes(reversed, condition.field, condition.arg);
            case "length" -> new Length(reversed, condition.field, condition.arg);
            case "contains" -> new Contains(reversed, condition.field, condition.arg);
            case "range" -> new Range(reversed, condition.field, condition.arg);
            case "true" -> new True(reversed, condition.field);
            default -> throw new RuntimeException("Unsupported condition type: " + t);
        };
    }
}