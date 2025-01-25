import { Rule as ConfigRule } from "../config";
import Condition from "./condition";
import { buildRuntimeCondition } from "./utils";

export default class Rule {
    readonly condition: Condition;
    readonly id?: number;
    readonly errorMessage?: string;

    constructor(rule: ConfigRule) {
        this.condition = buildRuntimeCondition(rule.condition);
        this.id = rule.id;
        this.errorMessage = rule.errorMessage;
    }
}