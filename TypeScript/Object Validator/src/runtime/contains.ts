import Condition from "./condition";

export default class Contains extends Condition {
    private readonly _arg: string | null;

    constructor(reversed: boolean, fieldExpression: string | undefined, arg: string) {
        super(reversed, fieldExpression);
        this._arg = arg;
    }

    protected override isFulfilledBy(value: any): boolean {
        if (typeof value === "string")
            return value.includes(this._arg!);
        if (value instanceof Object && typeof value[Symbol.iterator] === "function") {
            for (const o of value)
                if (o === undefined)
                    continue;
                else if (o === null) {
                    if (this._arg === null)
                        return true;
                } else if ((o as object).toString() === this._arg)
                    return true;
            return false;
        }
        throw "Unsupported type for 'contains': " + (typeof value);
    }
}