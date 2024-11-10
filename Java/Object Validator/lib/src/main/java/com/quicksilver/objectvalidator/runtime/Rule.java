package com.quicksilver.objectvalidator.runtime;

public class Rule {
    public final Condition condition;
    public final Integer id;
    public final String errorMessage;

    public Rule(com.quicksilver.objectvalidator.config.Rule rule) {
        this.condition = Utils.buildRuntimeCondition(rule.condition);
        this.id = rule.id;
        this.errorMessage = rule.errorMessage;
    }
}
