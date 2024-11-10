import Condition from "./condition";

export default class Null extends Condition {
    protected override isFulfilledBy(field: any): boolean {
        return field == null;
    }
}