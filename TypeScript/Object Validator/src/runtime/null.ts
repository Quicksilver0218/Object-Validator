import Condition from "./condition";

export default class Null extends Condition {
    protected override isFulfilledBy(value: unknown): boolean {
        return value == null;
    }
}