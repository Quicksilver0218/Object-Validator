export default abstract class Condition {
    protected readonly reversed: boolean;
    protected readonly fieldExpression?: string;

    constructor(reversed: boolean, fieldExpression?: string) {
        this.reversed = reversed;
        this.fieldExpression = fieldExpression;
    }

    private static handleField(obj: object, field: string, fields: object[]) {
        fields.push(obj[field]);
    }

    private static handleIndex(list: any[], index: number, fields: object[]) {
        fields.push(list[index]);
    }

    private static handleKey(map: Map<any, any>, key: string, fields: object[]) {
        fields.push(map.get(key));
    }

    private static handleFields(fields: object[], name: string, newFields: object[]) {
        for (const o of fields)
            if (o == null)
                newFields.push(o);
            else if (o instanceof Map)
                this.handleKey(o, name, newFields);
            else {
                if (o instanceof Array) {
                    const index = parseInt(name);
                    if (!isNaN(index)) {
                        this.handleIndex(o, index, newFields);
                        return;
                    }
                }
                this.handleField(o, name, newFields);
            }
    }

    check(root: any, rootFieldExpression: string | null, passedFields: Set<string>, failedFields: Set<string>): boolean {
        let fields = [root];
        if (this.fieldExpression != null) {
            if (root != null) {
                let fullName = "";
                for (const name of this.fieldExpression.split(".")) {
                    const newFields: any[] = [];
                    fullName += name;
                    if (fullName === "*")
                        for (const o of fields)
                            if (o == null)
                                newFields.push(o);
                            else if (typeof o[Symbol.iterator] === "function")
                                for (const item of o)
                                    newFields.push(item);
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
                                for (const o of fields)
                                    if (o == null)
                                        newFields.push(o);
                                    else
                                        Condition.handleField(o, fullName, newFields);
                                break;
                            case "I":
                                for (const o of fields)
                                    if (o == null)
                                        newFields.push(o);
                                    else
                                        Condition.handleIndex(o, parseInt(fullName), newFields);
                                break;
                            case "K":
                                for (const o of fields)
                                    if (o == null)
                                        newFields.push(o);
                                    else
                                        Condition.handleKey(o, fullName, newFields);
                                break;
                            case "*":
                                Condition.handleFields(fields, fullName + "*", newFields);
                                break;
                            default:
                                throw "Unsupported suffix: " + name[name.length - 1];
                        }
                    } else
                        Condition.handleFields(fields, name, newFields);
                    fields = newFields;
                    fullName = "";
                }
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