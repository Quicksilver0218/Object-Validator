package com.quicksilver.objectvalidator.runtime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;

public class Range extends Condition {
    private final String range;

    public Range(boolean reversed, String fieldExpression, String range) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    @Override
    protected boolean isFulfilledBy(Object field, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        if (field == null)
            throw new RuntimeException("Null values are not supported for 'range'.");
        if (field instanceof Byte || field instanceof Short || field instanceof Integer || field instanceof Long)
            return Utils.inRange(((Number)field).longValue(), range);
        if (field instanceof BigInteger bi) {
            for (String s : range.split(",")) {
                String limit = s.trim();
                if (limit.startsWith("[")) {
                    if (bi.compareTo(new BigInteger(limit.substring(1))) == -1)
                        return false;
                } else if (limit.startsWith("(")) {
                    if (bi.compareTo(new BigInteger(limit.substring(1))) != 1)
                        return false;
                } else if (limit.endsWith("]")) {
                    if (bi.compareTo(new BigInteger(limit.substring(0, limit.length() - 1))) == 1)
                        return false;
                } else if (limit.endsWith(")")) {
                    if (bi.compareTo(new BigInteger(limit.substring(0, limit.length() - 1))) != -1)
                        return false;
                } else if (!bi.equals(new BigInteger(limit)))
                    return false;
            }
            return true;
        }
        if (field instanceof Float || field instanceof Double) {
            double num = ((Number)field).doubleValue();
            for (String s : range.split(",")) {
                String limit = s.trim();
                if (limit.startsWith("[")) {
                    if (num < Double.parseDouble(limit.substring(1)))
                        return false;
                } else if (limit.startsWith("(")) {
                    if (num <= Double.parseDouble(limit.substring(1)))
                        return false;
                } else if (limit.endsWith("]")) {
                    if (num > Double.parseDouble(limit.substring(0, limit.length() - 1)))
                        return false;
                } else if (limit.endsWith(")")) {
                    if (num >= Double.parseDouble(limit.substring(0, limit.length() - 1)))
                        return false;
                } else if (num != Double.parseDouble(limit))
                    return false;
            }
            return true;
        }
        if (field instanceof BigDecimal bd) {
            for (String s : range.split(",")) {
                String limit = s.trim();
                if (limit.startsWith("[")) {
                    if (bd.compareTo(new BigDecimal(limit.substring(1))) == -1)
                        return false;
                } else if (limit.startsWith("(")) {
                    if (bd.compareTo(new BigDecimal(limit.substring(1))) != 1)
                        return false;
                } else if (limit.endsWith("]")) {
                    if (bd.compareTo(new BigDecimal(limit.substring(0, limit.length() - 1))) == 1)
                        return false;
                } else if (limit.endsWith(")")) {
                    if (bd.compareTo(new BigDecimal(limit.substring(0, limit.length() - 1))) != -1)
                        return false;
                } else if (!bd.equals(new BigDecimal(limit)))
                    return false;
            }
            return true;
        }
        if (field instanceof Date d) {
            for (String s : range.split(",")) {
                String limit = s.trim();
                if (limit.startsWith("[")) {
                    if (d.compareTo(Date.from(ZonedDateTime.parse(limit.substring(1)).toInstant())) == -1)
                        return false;
                } else if (limit.startsWith("(")) {
                    if (d.compareTo(Date.from(ZonedDateTime.parse(limit.substring(1)).toInstant())) != 1)
                        return false;
                } else if (limit.endsWith("]")) {
                    if (d.compareTo(Date.from(ZonedDateTime.parse(limit.substring(0, limit.length() - 1)).toInstant())) == 1)
                        return false;
                } else if (limit.endsWith(")")) {
                    if (d.compareTo(Date.from(ZonedDateTime.parse(limit.substring(0, limit.length() - 1)).toInstant())) != -1)
                        return false;
                } else if (!d.equals(Date.from(ZonedDateTime.parse(limit).toInstant())))
                    return false;
            }
            return true;
        }
        throw new RuntimeException("Unsupported type for 'range': " + field.getClass());
    }
}
