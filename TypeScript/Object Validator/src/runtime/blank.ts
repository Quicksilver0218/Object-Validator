import Condition from "./condition";

export default class Blank extends Condition {
    protected override isFulfilledBy(field: any): boolean {
        if (typeof field === "string")
            return field.trim().length === 0;
        throw "Unsupported type for 'blank': " + (typeof field);
    }
}