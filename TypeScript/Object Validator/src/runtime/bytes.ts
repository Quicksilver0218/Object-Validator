import Condition from "./condition";
import { inRange } from "./utils";

export default class Bytes extends Condition {
    private readonly _range: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, range: string) {
        super(reversed, fieldExpression);
        this._range = range;
    }

    protected override isFulfilledBy(value: any): boolean {
        if (typeof value === "string")
            return inRange(new Blob([value]).size, this._range);
        throw "Unsupported type for 'bytes': " + (typeof value);
    }
}