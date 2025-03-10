function handleField(obj: object, field: string, values: unknown[]) {
    values.push(obj[field]);
}

function handleIndex(list: { [key: number]: unknown }, index: number, values: unknown[]) {
    values.push(list[index]);
}

function handleKey(map: Map<unknown, unknown>, key: string, values: unknown[]) {
    values.push(map.get(key));
}

function handleValues(values: unknown[], name: string, newValues: unknown[]) {
    for (const value of values)
        if (value == null)
            newValues.push(value);
        else if (value instanceof Map)
            handleKey(value, name, newValues);
        else {
            if (value instanceof Array) {
                const index = parseInt(name);
                if (!isNaN(index)) {
                    handleIndex(value, index, newValues);
                    return;
                }
            }
            handleField(value, name, newValues);
        }
}

export default abstract class Condition {
    private readonly _reversed: boolean;
    private readonly _fieldExpression?: string;

    constructor(reversed: boolean, fieldExpression?: string) {
        this._reversed = reversed;
        this._fieldExpression = fieldExpression;
    }

    check(root: unknown, rootFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean {
        let values = [root];
        if (this._fieldExpression != null) {
            if (root != null) {
                let fullName = "";
                for (const name of this._fieldExpression.split(".")) {
                    const newValues: unknown[] = [];
                    fullName += name;
                    if (fullName === "*")
                        for (const o of values)
                            if (o == null)
                                newValues.push(o);
                            else if (typeof o[Symbol.iterator] === "function")
                                for (const item of o as Iterable<unknown>)
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
                                        handleField(o, fullName, newValues);
                                break;
                            case "I":
                                for (const o of values)
                                    if (o == null)
                                        newValues.push(o);
                                    else
                                        handleIndex(o as { [key: number]: unknown }, parseInt(fullName), newValues);
                                break;
                            case "K":
                                for (const o of values)
                                    if (o == null)
                                        newValues.push(o);
                                    else
                                        handleKey(o as Map<unknown, unknown>, fullName, newValues);
                                break;
                            case "*":
                                handleValues(values, fullName + "*", newValues);
                                break;
                            default:
                                throw "Unsupported suffix: " + name[name.length - 1];
                        }
                    } else
                        handleValues(values, name, newValues);
                    values = newValues;
                    fullName = "";
                }
            }
            if (rootFieldExpression !== null)
                rootFieldExpression += "." + this._fieldExpression;
            else
                rootFieldExpression = this._fieldExpression;
        }
        const childPassedFields = new Set<string>(), childFailedFields = new Set<string>();
        for (const value of values)
            if (this.isFulfilledBy(value, rootFieldExpression, childPassedFields, childFailedFields) == this._reversed) {
                if (rootFieldExpression !== null)
                    failedFields.add(rootFieldExpression);
                if (this._reversed)
                    childPassedFields.forEach(a => failedFields.add(a));
                else
                    childFailedFields.forEach(a => failedFields.add(a));
                return false;
            }
        if (rootFieldExpression !== null)
            passedFields.add(rootFieldExpression);
        if (this._reversed)
            childFailedFields.forEach(a => passedFields.add(a));
        else
            childPassedFields.forEach(a => passedFields.add(a));
        return true;
    }

    protected abstract isFulfilledBy(value: unknown, fullFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean;
}