import Condition from "./condition";

export default class Null extends Condition {
    protected override isFulfilledBy(value: any): boolean {
        return value == null;
    }
}