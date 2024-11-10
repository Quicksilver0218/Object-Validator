import Condition from "./condition";
import Utils from "./utils";

export default class Bytes extends Condition {
    private readonly range: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, range: string) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    protected override isFulfilledBy(field: any): boolean {
        if (typeof field === "string")
            return Utils.inRange(new Blob([field]).size, this.range);
        throw "Unsupported type for 'bytes': " + (typeof field);
    }
}