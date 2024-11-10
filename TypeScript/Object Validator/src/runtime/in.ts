import Condition from "./condition";

export default class In extends Condition {
    private readonly args: (string | null)[];

    constructor(reversed: boolean, fieldExpression: string | undefined, args: (string | null)[]) {
        super(reversed, fieldExpression);
        this.args = args;
    }

    protected override isFulfilledBy(field: any): boolean {
        if (typeof field === "undefined")
            return false;
        if (field === null) {
            for (const arg of this.args)
                if (arg === null)
                    return true;
            return false;
        }
        for (const arg of this.args)
            if ((field as object).toString() === arg)
                return true;
        return false;
    }
}