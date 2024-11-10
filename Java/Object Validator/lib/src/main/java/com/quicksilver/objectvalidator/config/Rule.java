package com.quicksilver.objectvalidator.config;

public class Rule {
    public final Condition condition;
    public final Integer id;
    public final String errorMessage;

    public Rule(Condition condition, Integer id, String errorMessage) {
        this.condition = condition;
        this.id = id;
        this.errorMessage = errorMessage;
    }

    @SuppressWarnings("unused")
    private Rule() {
        this(null, null, null);
    }
}
