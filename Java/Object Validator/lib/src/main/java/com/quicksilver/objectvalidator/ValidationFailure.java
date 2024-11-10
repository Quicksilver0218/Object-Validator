package com.quicksilver.objectvalidator;

public class ValidationFailure {
    public final Integer id;
    public final String message;

    public ValidationFailure(Integer id, String message) {
        this.id = id;
        this.message = message;
    }
}
