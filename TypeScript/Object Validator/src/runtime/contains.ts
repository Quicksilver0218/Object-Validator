import Condition from "./condition";

export default class Contains extends Condition {
    private readonly _arg: string | null;

    constructor(reversed: boolean, fieldExpression: string | undefined, arg: string) {
        super(reversed, fieldExpression);
        this._arg = arg;
    }

    protected override isFulfilledBy(value: unknown): boolean {
        if (typeof value === "string") {
            if (this._arg == null)
                throw "Null argument is not supported for 'contains' with string values.";
            return value.includes(this._arg);
        }
        if (value instanceof Object && typeof (value as Record<symbol, unknown>)[Symbol.iterator] === "function") {
            for (const o of value as Iterable<unknown>)
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