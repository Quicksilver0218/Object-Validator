import Condition from "./condition";

export default class In extends Condition {
    private readonly _args: (string | null)[];

    constructor(reversed: boolean, fieldExpression: string | undefined, args: (string | null)[]) {
        super(reversed, fieldExpression);
        this._args = args;
    }

    protected override isFulfilledBy(value: any): boolean {
        if (value === undefined)
            return false;
        if (value === null) {
            for (const arg of this._args)
                if (arg === null)
                    return true;
            return false;
        }
        for (const arg of this._args)
            if ((value as object).toString() === arg)
                return true;
        return false;
    }
}