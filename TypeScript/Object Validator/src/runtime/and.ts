import Condition from "./condition";

export default class And extends Condition {
    private readonly _conditions: Condition[];

    constructor(reversed: boolean, fieldExpression: string | undefined | undefined, conditions: Condition[]) {
        super(reversed, fieldExpression);
        this._conditions = conditions;
    }

    protected override isFulfilledBy(value: unknown, fullFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean {
        const newPassedFields = new Set<string>();
        for (const condition of this._conditions)
            if (!condition.check(value, fullFieldExpression, newPassedFields, failedFields))
                return false;
        newPassedFields.forEach(a => passedFields.add(a));
        return true;
    }
}