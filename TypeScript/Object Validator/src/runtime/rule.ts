import { Rule as ConfigRule } from "../config";
import Condition from "./condition";
import Utils from "./utils";

export default class Rule {
    readonly condition: Condition;
    readonly id?: number;
    readonly errorMessage?: string;

    constructor(rule: ConfigRule) {
        this.condition = Utils.buildRuntimeCondition(rule.condition);
        this.id = rule.id;
        this.errorMessage = rule.errorMessage;
    }
}