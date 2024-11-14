export default abstract class Condition {
    protected readonly reversed: boolean;
    protected readonly fieldExpression?: string;

    constructor(reversed: boolean, fieldExpression?: string) {
        this.reversed = reversed;
        this.fieldExpression = fieldExpression;
    }

    private static handleField(obj: object, field: string, values: object[]) {
        values.push(obj[field]);
    }

    private static handleIndex(list: any[], index: number, values: object[]) {
        values.push(list[index]);
    }

    private static handleKey(map: Map<any, any>, key: string, values: object[]) {
        values.push(map.get(key));
    }

    private static handleValues(values: object[], name: string, newValues: object[]) {
        for (const value of values)
            if (value == null)
                newValues.push(value);
            else if (value instanceof Map)
                this.handleKey(value, name, newValues);
            else {
                if (value instanceof Array) {
                    const index = parseInt(name);
                    if (!isNaN(index)) {
                        this.handleIndex(value, index, newValues);
                        return;
                    }
                }
                this.handleField(value, name, newValues);
            }
    }

    check(root: any, rootFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean {
        let values = [root];
        if (this.fieldExpression != null) {
            if (root != null) {
                let fullName = "";
                for (const name of this.fieldExpression.split(".")) {
                    const newValues: any[] = [];
                    fullName += name;
                    if (fullName === "*")
                        for (const o of values)
                            if (o == null)
                                newValues.push(o);
                            else if (typeof o[Symbol.iterator] === "function")
                                for (const item of o)
                                    newValues.push(item);
                            else
                                throw "Unsupported type for iteration: " + (typeof o);
                    else if (name.length >= 3 && name.substring(name.length - 3, name.length - 1) === "//")
                        fullName = fullName.substring(0, name.length - 3) + fullName.substring(fullName.length - 2);
                    else if (name.length >= 2 && name[name.length - 2] === "/") {
                        fullName = fullName.substring(0, fullName.length - 2);
                        switch (name[name.length - 1].toUpperCase()) {
                            case "C":
                                fullName += ".";
                                continue;
                            case "F":
                                for (const o of values)
                                    if (o == null)
                                        newValues.push(o);
                                    else
                                        Condition.handleField(o, fullName, newValues);
                                break;
                            case "I":
                                for (const o of values)
                                    if (o == null)
                                        newValues.push(o);
                                    else
                                        Condition.handleIndex(o, parseInt(fullName), newValues);
                                break;
                            case "K":
                                for (const o of values)
                                    if (o == null)
                                        newValues.push(o);
                                    else
                                        Condition.handleKey(o, fullName, newValues);
                                break;
                            case "*":
                                Condition.handleValues(values, fullName + "*", newValues);
                                break;
                            default:
                                throw "Unsupported suffix: " + name[name.length - 1];
                        }
                    } else
                        Condition.handleValues(values, name, newValues);
                    values = newValues;
                    fullName = "";
                }
            }
            if (rootFieldExpression !== null)
                rootFieldExpression += "." + this.fieldExpression;
            else
                rootFieldExpression = this.fieldExpression;
        }
        const childPassedFields = new Set<string>(), childFailedFields = new Set<string>();
        for (const value of values)
            if (this.isFulfilledBy(value, rootFieldExpression, childPassedFields, childFailedFields) == this.reversed) {
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

    protected abstract isFulfilledBy(value: any, fullFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean;
}