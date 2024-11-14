package com.quicksilver.objectvalidator.runtime;

import java.util.HashSet;

public class Bytes extends Condition {
    private final String range;

    public Bytes(boolean reversed, String fieldExpression, String range) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    private static int utf8ByteLength(CharSequence sequence) {
        int count = 0;
        for (int i = 0, len = sequence.length(); i < len; i++) {
            char c = sequence.charAt(i);
            if (c <= 0x7F)
                count++;
            else if (c <= 0x7FF)
                count += 2;
            else if (Character.isHighSurrogate(c)) {
                count += 4;
                i++;
            } else
                count += 3;
        }
        return count;
    }

    @Override
    protected boolean isFulfilledBy(Object value, String fullFieldExpression, HashSet<String> passedFields, HashSet<String> failedFields) {
        if (value == null)
            throw new RuntimeException("Null values are not supported for 'bytes'.");
        if (value instanceof CharSequence s)
            return Utils.inRange(utf8ByteLength(s), range);
        throw new RuntimeException("Unsupported type for 'bytes': " + value.getClass());
    }
}