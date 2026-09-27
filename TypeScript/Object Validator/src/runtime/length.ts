import Condition from "./condition";
import { inRange } from "./utils";

export default class Length extends Condition {
    private readonly _range: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, range: string) {
        super(reversed, fieldExpression);
        this._range = range;
    }

    protected override isFulfilledBy(value: unknown): boolean {
        if (typeof value === "string")
            return inRange(value.length, this._range);
        if (value instanceof Object) {
            if (Number.isInteger((value as { length?: number })["length"]))
                return inRange((value as { length?: number })["length"], this._range);
            if (Number.isInteger((value as { size?: number })["size"]))
                return inRange((value as { size?: number })["size"], this._range);
        }
        throw "Unsupported type for 'length': " + (typeof value);
    }
}