import Condition from "./condition";

export default class Blank extends Condition {
    protected override isFulfilledBy(value: any): boolean {
        if (typeof value === "string")
            return value.trim().length === 0;
        throw "Unsupported type for 'blank': " + (typeof value);
    }
}