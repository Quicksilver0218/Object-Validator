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

export default class Utils {
    static inRange(num: number, range: string): boolean {
        for (const s of range.split(",")) {
            const limit = s.trim();
            if (limit.startsWith("[")) {
                if (num < parseInt(limit.substring(1)))
                    return false;
            } else if (limit.startsWith("(")) {
                if (num <= parseInt(limit.substring(1)))
                    return false;
            } else if (limit.endsWith("]")) {
                if (num > parseInt(limit.substring(0, limit.length - 1)))
                    return false;
            } else if (limit.endsWith(")")) {
                if (num >= parseInt(limit.substring(0, limit.length - 1)))
                    return false;
            } else if (num !== parseInt(limit))
                return false;
        }
        return true;
    }

    static buildRuntimeCondition(condition: Condition): RuntimeCondition {
        let t = condition.type.trim().toLowerCase();
        let reversed = false;
        while (t.startsWith("!")) {
            reversed = !reversed;
            t = t.substring(1).trim();
        }
        switch (t) {
            case "and":
                return new And(reversed, condition.field, condition.conditions!.map(c => this.buildRuntimeCondition(c)));
            case "or":
                return new Or(reversed, condition.field, condition.conditions!.map(c => this.buildRuntimeCondition(c)));
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
}