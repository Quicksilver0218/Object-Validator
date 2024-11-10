import Condition from "./condition";

export default class Range extends Condition {
    private readonly range: string;

    constructor(reversed: boolean, fieldExpression: string | undefined, range: string) {
        super(reversed, fieldExpression);
        this.range = range;
    }

    protected override isFulfilledBy(field: any): boolean {
        if (typeof field === "number") {
            for (const s of this.range.split(",")) {
                const limit = s.trim();
                if (limit.startsWith("[")) {
                    if (field < parseFloat(limit.substring(1)))
                        return false;
                } else if (limit.startsWith("(")) {
                    if (field <= parseFloat(limit.substring(1)))
                        return false;
                } else if (limit.endsWith("]")) {
                    if (field > parseFloat(limit.substring(0, limit.length - 1)))
                        return false;
                } else if (limit.endsWith(")")) {
                    if (field >= parseFloat(limit.substring(0, limit.length - 1)))
                        return false;
                } else if (field !== parseFloat(limit))
                    return false;
            }
            return true;
        }
        if (field instanceof Date) {
            const d = field as Date;
            for (const s of this.range.split(",")) {
                const limit = s.trim();
                if (limit.startsWith("[")) {
                    if (d < new Date(limit.substring(1)))
                        return false;
                } else if (limit.startsWith("(")) {
                    if (d <= new Date(limit.substring(1)))
                        return false;
                } else if (limit.endsWith("]")) {
                    if (d > new Date(limit.substring(0, limit.length - 1)))
                        return false;
                } else if (limit.endsWith(")")) {
                    if (d >= new Date(limit.substring(0, limit.length - 1)))
                        return false;
                } else if (+d !== +new Date(limit))
                    return false;
            }
            return true;
        }
        throw "Unsupported type for 'range': " + (typeof field);
    }
}