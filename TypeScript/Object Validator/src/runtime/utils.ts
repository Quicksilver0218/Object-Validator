import { Condition } from "../config";
import RuntimeCondition from "./condition";
import And from "./and";
import Blank from "./blank";
import Bytes from "./bytes";
import Contains from "./contains";
import In from "./in";
import Length from "./length";
import Null from "./null";
import Or from "./or";
import Range from "./range";
import RegEx from "./reg-ex";
import True from "./true";

function inLimit(value: any, operator: string, target: any, reversed: boolean): boolean {
    switch (operator) {
        case "<":
            return value < target !== reversed;
        case ">":
            return value > target !== reversed;
        case "=":
            return +value === +target !== reversed;
        default:
            throw "Unsupported operator: " + operator;
    }
}

export function inRange(value: any, range: string): boolean {
    for (let limit of range.split(",")) {
        limit = limit.trim();
        let operator: string;
        let targetString: string;
        let reversed: boolean;
        if (limit.startsWith("[")) {
            operator = "<";
            targetString = limit.substring(1);
            reversed = false;
        } else if (limit.startsWith("(")) {
            operator = ">";
            targetString = limit.substring(1);
            reversed = true;
        } else if (limit.endsWith("]")) {
            operator = ">";
            targetString = limit.substring(0, limit.length - 1);
            reversed = false;
        } else if (limit.endsWith(")")) {
            operator = "<";
            targetString = limit.substring(0, limit.length - 1);
            reversed = true;
        } else {
            operator = "=";
            targetString = limit;
            reversed = true;
        }
        if (typeof value === "number") {
            if (inLimit(value, operator, parseFloat(targetString), reversed))
                return false;
        } else if (typeof value === "bigint") {
            if (inLimit(value, operator, BigInt(targetString), reversed))
                return false;
        } else if (value instanceof Date) {
            if (inLimit(value, operator, new Date(targetString), reversed))
                return false;
        } else
            throw "Unsupported type for 'inRange()': " + (typeof value);
    }
    return true;
}

export function buildRuntimeCondition(condition: Condition): RuntimeCondition {
    let t = condition.type.trim().toLowerCase();
    let reversed = false;
    while (t.startsWith("!")) {
        reversed = !reversed;
        t = t.substring(1).trim();
    }
    switch (t) {
        case "and":
            return new And(reversed, condition.field, condition.conditions!.map(c => buildRuntimeCondition(c)));
        case "or":
            return new Or(reversed, condition.field, condition.conditions!.map(c => buildRuntimeCondition(c)));
        case "null":
            return new Null(reversed, condition.field);
        case "in":
            return new In(reversed, condition.field, condition.args!);
        case "blank":
            return new Blank(reversed, condition.field);
        case "regex":
            return new RegEx(reversed, condition.field, condition.arg!);
        case "bytes":
            return new Bytes(reversed, condition.field, condition.arg!);
        case "length":
            return new Length(reversed, condition.field, condition.arg!);
        case "contains":
            return new Contains(reversed, condition.field, condition.arg!);
        case "range":
            return new Range(reversed, condition.field, condition.arg!);
        case "true":
            return new True(reversed, condition.field);
        default:
            throw "Unsupported condition type: " + t;
    }
}