package com.quicksilver.objectvalidator.runtime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;

class Utils {
    private static <T extends Comparable<T>> boolean inLimit(T value, char operator, T target, boolean reversed) {
        int result = value.compareTo(target);
        return switch (operator) {
            case '<' -> result < 0 ^ reversed;
            case '>' -> result > 0 ^ reversed;
            case '=' -> result == 0 ^ reversed;
            default -> throw new RuntimeException("Unsupported operator: " + operator);
        };
    }

    static boolean inRange(Object value, String range) {
        for (String limit : range.split(",")) {
            limit = limit.trim();
            char operator;
            String targetString;
            boolean reversed;
            if (limit.startsWith("[")) {
                operator = '<';
                targetString = limit.substring(1);
                reversed = false;
            } else if (limit.startsWith("(")) {
                operator = '>';
                targetString = limit.substring(1);
                reversed = true;
            } else if (limit.endsWith("]")) {
                operator = '>';
                targetString = limit.substring(0, limit.length() - 1);
                reversed = false;
            } else if (limit.endsWith(")")) {
                operator = '<';
                targetString = limit.substring(0, limit.length() - 1);
                reversed = true;
            } else {
                operator = '=';
                targetString = limit;
                reversed = true;
            }
            switch (value) {
                case Byte b -> {
                    if (inLimit(b, operator, Byte.parseByte(targetString), reversed))
                        return false;
                }
                case Short s -> {
                    if (inLimit(s, operator, Short.parseShort(targetString), reversed))
                        return false;
                }
                case Integer i -> {
                    if (inLimit(i, operator, Integer.parseInt(targetString), reversed))
                        return false;
                }
                case Long l -> {
                    if (inLimit(l, operator, Long.parseLong(targetString), reversed))
                        return false;
                }
                case BigInteger bi -> {
                    if (inLimit(bi, operator, new BigInteger(targetString), reversed))
                        return false;
                }
                case Float f -> {
                    if (inLimit(f, operator, Float.parseFloat(targetString), reversed))
                        return false;
                }
                case Double d -> {
                    if (inLimit(d, operator, Double.parseDouble(targetString), reversed))
                        return false;
                }
                case BigDecimal bd -> {
                    if (inLimit(bd, operator, new BigDecimal(targetString), reversed))
                        return false;
                }
                case Date d -> {
                    if (inLimit(d, operator, Date.from(ZonedDateTime.parse(targetString).toInstant()), reversed))
                        return false;
                }
                default -> throw new RuntimeException("Unsupported type for 'inRange()': " + value.getClass());
            }
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