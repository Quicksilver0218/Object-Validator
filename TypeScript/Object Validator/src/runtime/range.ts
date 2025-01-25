import Condition from "./condition";
import { inRange } from "./utils";

export default class Range extends Condition {
    private readonly _range: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, range: string) {
        super(reversed, fieldExpression);
        this._range = range;
    }

    protected override isFulfilledBy(value: any): boolean {
        if (!(typeof value === "number" || typeof value === "bigint" || value instanceof Date))
            throw "Unsupported type for 'range': " + (typeof value);
        return inRange(value, this._range);
    }
}