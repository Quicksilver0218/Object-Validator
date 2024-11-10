package com.quicksilver.objectvalidator.config;

public class Condition {
    public final String type, field, arg;
    public final String[] args;
    public final Condition[] conditions;

    public Condition(String type, String field, String arg, String[] args, Condition[] conditions) {
        this.type = type;
        this.field = field;
        this.arg = arg;
        this.args = args;
        this.conditions = conditions;
    }

    @SuppressWarnings("unused")
    private Condition() {
        this(null, null, null, null, null);
    }
}
