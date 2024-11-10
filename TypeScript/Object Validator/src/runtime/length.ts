import Condition from "./condition";
import Utils from "./utils";

export default class Length extends Condition {
    private readonly range: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, range: string) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    protected override isFulfilledBy(field: any): boolean {
        if (typeof field === "string")
            return Utils.inRange(field.length, this.range);
        if (field instanceof Object) {
            if (Number.isInteger(field["length"]))
                return Utils.inRange(field["length"], this.range);
            if (Number.isInteger(field["size"]))
                return Utils.inRange(field["size"], this.range);
        }
        throw "Unsupported type for 'length': " + (typeof field);
    }
}