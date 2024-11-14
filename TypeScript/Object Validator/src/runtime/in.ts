import Condition from "./condition";

export default class In extends Condition {
    private readonly args: (string | null)[];

    constructor(reversed: boolean, fieldExpression: string | undefined, args: (string | null)[]) {
        super(reversed, fieldExpression);
        this.args = args;
    }

    protected override isFulfilledBy(value: any): boolean {
        if (typeof value === "undefined")
            return false;
        if (value === null) {
            for (const arg of this.args)
                if (arg === null)
                    return true;
            return false;
        }
        for (const arg of this.args)
            if ((value as object).toString() === arg)
                return true;
        return false;
    }
}