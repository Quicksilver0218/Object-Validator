import Condition from "./condition";

export default class Or extends Condition {
    protected readonly conditions: Condition[]

    constructor(reversed: boolean, fieldExpression: string | undefined | undefined, conditions: Condition[]) {
        super(reversed, fieldExpression);
        this.conditions = conditions;
    }

    protected override isFulfilledBy(value: any, fullFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean {
        const newFailedFields = new Set<string>();
        for (const condition of this.conditions)
            if (condition.check(value, fullFieldExpression, passedFields, newFailedFields))
                return true;
        newFailedFields.forEach(a => failedFields.add(a));
        return false;
    }
}