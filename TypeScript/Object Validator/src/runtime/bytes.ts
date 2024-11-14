import Condition from "./condition";
import Utils from "./utils";

export default class Bytes extends Condition {
    private readonly range: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, range: string) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    protected override isFulfilledBy(value: any): boolean {
        if (typeof value === "string")
            return Utils.inRange(new Blob([value]).size, this.range);
        throw "Unsupported type for 'bytes': " + (typeof value);
    }
}