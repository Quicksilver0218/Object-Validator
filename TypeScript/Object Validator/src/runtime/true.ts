import Condition from "./condition";

export default class True extends Condition {
    protected override isFulfilledBy(field: any): boolean {
        return field === true;
    }
}