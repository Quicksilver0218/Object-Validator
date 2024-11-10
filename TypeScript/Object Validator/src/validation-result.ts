import ValidationFailure from "./validation-failure";

export default class ValidationResult {
    readonly passed: boolean;
    readonly failedFields: Set<string>;
    readonly failures: ValidationFailure[];

    constructor(passed: boolean, failedFields: Set<string>, failures: ValidationFailure[]) {
        this.passed = passed;
        this.failedFields = failedFields;
        this.failures = failures;
    }
}