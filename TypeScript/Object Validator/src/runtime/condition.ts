export default abstract class Condition {
    protected readonly reversed: boolean;
    protected readonly fieldExpression?: string;

    constructor(reversed: boolean, fieldExpression?: string) {
        this.reversed = reversed;
        this.fieldExpression = fieldExpression;
    }

    check(root: any, rootFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean {
        let fields = [root];
        if (this.fieldExpression != null) {
            if (typeof root !== "undefined")
                for (let name of this.fieldExpression.split(".")) {
                    const newFields: any[] = [];
                    if (name === "*")
                        for (const o of fields)
                            if (o == null)
                                newFields.push(o);
                            else if (typeof o[Symbol.iterator] === "function")
                                for (const item of o)
                                    newFields.push(item);
                            else
                                throw "Unsupported type for iteration: " + (typeof o);
                    else {
                        let allAsterisk = true;
                        for (const c of name)
                            if (c != '*') {
                                allAsterisk = false;
                                break;
                            }
                        if (allAsterisk)
                            name = name.substring(1);
                        for (const o of fields)
                            if (o == null)
                                newFields.push(o);
                            else if (o instanceof Map)
                                newFields.push(o.get(name));
                            else
                                newFields.push(o[name]);
                    }
                    fields = newFields;
                }
            if (rootFieldExpression !== null)
                rootFieldExpression += "." + this.fieldExpression;
            else
                rootFieldExpression = this.fieldExpression;
        }
        const childPassedFields = new Set<string>(), childFailedFields = new Set<string>();
        for (const field of fields)
            if (this.isFulfilledBy(field, rootFieldExpression, childPassedFields, childFailedFields) == this.reversed) {
                if (rootFieldExpression !== null)
                    failedFields.add(rootFieldExpression);
                if (this.reversed)
                    childPassedFields.forEach(a => failedFields.add(a));
                else
                    childFailedFields.forEach(a => failedFields.add(a));
                return false;
            }
        if (rootFieldExpression !== null)
            passedFields.add(rootFieldExpression);
        if (this.reversed)
            childFailedFields.forEach(a => passedFields.add(a));
        else
            childPassedFields.forEach(a => passedFields.add(a));
        return true;
    }

    protected abstract isFulfilledBy(field: any, fullFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean;
}