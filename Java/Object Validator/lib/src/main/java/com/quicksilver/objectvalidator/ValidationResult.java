package com.quicksilver.objectvalidator;

import java.util.Collection;
import java.util.Set;

public class ValidationResult {
    public final boolean passed;
    public final Set<String> failedFields;
    public final Collection<ValidationFailure> failures;

    public ValidationResult(boolean passed, Set<String> failedFields, Collection<ValidationFailure> failures) {
        this.passed = passed;
        this.failedFields = failedFields;
        this.failures = failures;
    }
}
