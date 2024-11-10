import Condition from "./condition";

export default class Contains extends Condition {
    private readonly arg: string | null;

    constructor(reversed: boolean, fieldExpression: string | undefined, arg: string) {
        super(reversed, fieldExpression);
        this.arg = arg;
    }

    protected override isFulfilledBy(field: any): boolean {
        if (typeof field === "string")
            return field.includes(this.arg!);
        if (field instanceof Object && typeof field[Symbol.iterator] === "function") {
            for (const o of field)
                if (typeof o === "undefined")
                    continue;
                else if (o === null) {
                    if (this.arg === null)
                        return true;
                } else if ((o as object).toString() === this.arg)
                    return true;
            return false;
        }
        throw "Unsupported type for 'contains': " + (typeof field);
    }
}