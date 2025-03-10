import Condition from "./condition";

export default class True extends Condition {
    protected override isFulfilledBy(value: unknown): boolean {
        return value === true;
    }
}