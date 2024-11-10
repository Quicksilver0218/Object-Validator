import Condition from "./condition";

export default class And extends Condition {
    protected readonly conditions: Condition[];

    constructor(reversed: boolean, fieldExpression: string | undefined | undefined, conditions: Condition[]) {
        super(reversed, fieldExpression);
        this.conditions = conditions;
    }

    protected override isFulfilledBy(field: any, fullFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean {
        const newPassedFields = new Set<string>();
        for (const condition of this.conditions)
            if (!condition.check(field, fullFieldExpression, newPassedFields, failedFields))
                return false;
        newPassedFields.forEach(a => passedFields.add(a));
        return true;
    }
}