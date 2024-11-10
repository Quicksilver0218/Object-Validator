package com.quicksilver.objectvalidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quicksilver.objectvalidator.runtime.Rule;

public class Validator {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Rule[] rules;
    public boolean fastFail;

    public Validator(com.quicksilver.objectvalidator.config.Rule[] rules, boolean fastFail) {
        this.rules = Arrays.stream(rules).map(r -> new Rule(r)).toArray(Rule[]::new);
        this.fastFail = fastFail;
    }

    public Validator(com.quicksilver.objectvalidator.config.Rule[] rules) {
        this(rules, false);
    }

    public Validator(String rulesJson, boolean fastFail) throws JsonMappingException, JsonProcessingException {
        rules = Arrays.stream(objectMapper.readValue(rulesJson, com.quicksilver.objectvalidator.config.Rule[].class)).map(r -> new Rule(r)).toArray(Rule[]::new);
        this.fastFail = fastFail;
    }

    public Validator(String rulesJson) throws JsonMappingException, JsonProcessingException {
        this(rulesJson, false);
    }

    public ValidationResult validate(Object obj) throws ReflectiveOperationException {
        boolean valid = true;
        HashSet<String> failedFields = new HashSet<>();
        ArrayList<ValidationFailure> failures = new ArrayList<>();
        for (Rule rule : rules) {
            if (!rule.condition.check(obj, null, new HashSet<>(), failedFields)) {
                valid = false;
                if (rule.id != null || rule.errorMessage != null)
                    failures.add(new ValidationFailure(rule.id, rule.errorMessage));
                if (fastFail)
                    break;
            }
        }
        return new ValidationResult(valid, failedFields, failures);
    }
}
