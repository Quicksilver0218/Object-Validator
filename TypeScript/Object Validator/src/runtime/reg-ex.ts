import Condition from "./condition";

export default class RegEx extends Condition {
    private readonly pattern: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, pattern: string) {
        super(reversed, fieldExpression);
        this.pattern = pattern;
    }

    isFulfilledBy(field: any): boolean {
        if (typeof field === "string")
            return new RegExp(this.pattern).test(field);
        throw "Unsupported type for 'regex': " + (typeof field);
    }
}