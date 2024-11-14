import Condition from "./condition";
import Utils from "./utils";

export default class Range extends Condition {
    private readonly range: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, range: string) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    protected override isFulfilledBy(value: any): boolean {
        if (!(typeof value === "number" || typeof value === "bigint" || value instanceof Date))
            throw "Unsupported type for 'range': " + (typeof value);
        return Utils.inRange(value, this.range);
    }
}