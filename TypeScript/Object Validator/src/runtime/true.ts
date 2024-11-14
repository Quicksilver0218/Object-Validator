import Condition from "./condition";

export default class True extends Condition {
    protected override isFulfilledBy(value: any): boolean {
        return value === true;
    }
}