import { Rule } from "./config";
import RuntimeRule from "./runtime/rule";
import ValidationFailure from "./validation-failure";
import ValidationResult from "./validation-result";

export default class Validator {
    private readonly _rules: RuntimeRule[];
    fastFail: boolean;

    constructor(rules: Rule[], fastFail = false) { 
        this._rules = rules.map(r => new RuntimeRule(r));
        this.fastFail = fastFail;
    }

    validate(obj: any): ValidationResult {
        let valid = true;
        const failedFields = new Set<string>();
        const failures: ValidationFailure[] = [];
        for (const rule of this._rules) {
            if (!rule.condition.check(obj, null, new Set(), failedFields)) {
                valid = false;
                if (rule.id != null || rule.errorMessage != null)
                    failures.push(new ValidationFailure(rule.id, rule.errorMessage));
                if (this.fastFail)
                    break;
            }
        }
        return new ValidationResult(valid, failedFields, failures);
    }
}