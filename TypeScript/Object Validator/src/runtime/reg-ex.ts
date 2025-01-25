import Condition from "./condition";

export default class RegEx extends Condition {
    private readonly _pattern: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, pattern: string) {
        super(reversed, fieldExpression);
        this._pattern = pattern;
    }

    isFulfilledBy(value: any): boolean {
        if (typeof value === "string")
            return new RegExp(this._pattern).test(value);
        throw "Unsupported type for 'regex': " + (typeof value);
    }
}