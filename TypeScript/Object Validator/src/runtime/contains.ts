import Condition from "./condition";

export default class Contains extends Condition {
    private readonly arg: string | null;

    constructor(reversed: boolean, fieldExpression: string | undefined, arg: string) {
        super(reversed, fieldExpression);
        this.arg = arg;
    }

    protected override isFulfilledBy(value: any): boolean {
        if (typeof value === "string")
            return value.includes(this.arg!);
        if (value instanceof Object && typeof value[Symbol.iterator] === "function") {
            for (const o of value)
                if (typeof o === "undefined")
                    continue;
                else if (o === null) {
                    if (this.arg === null)
                        return true;
                } else if ((o as object).toString() === this.arg)
                    return true;
            return false;
        }
        throw "Unsupported type for 'contains': " + (typeof value);
    }
}