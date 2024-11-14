import Condition from "./condition";
import Utils from "./utils";

export default class Length extends Condition {
    private readonly range: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, range: string) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    protected override isFulfilledBy(value: any): boolean {
        if (typeof value === "string")
            return Utils.inRange(value.length, this.range);
        if (value instanceof Object) {
            if (Number.isInteger(value["length"]))
                return Utils.inRange(value["length"], this.range);
            if (Number.isInteger(value["size"]))
                return Utils.inRange(value["size"], this.range);
        }
        throw "Unsupported type for 'length': " + (typeof value);
    }
}