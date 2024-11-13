using System.Collections;

namespace Quicksilver.ObjectValidator.Runtime;
abstract class Condition(bool reversed, string? fieldExpression)
{
    protected readonly bool reversed = reversed;
    protected readonly string? fieldExpression = fieldExpression;

    private static void HandleField(object o, string field, List<object?> fields) {
        fields.Add(o.GetType().GetField(field)!.GetValue(o));
    }

    private static void HandleIndex(IList list, int index, List<object?> fields) {
        fields.Add(list[index]);
    }

    private static void HandleKey(IDictionary map, string key, List<object?> fields) {
        fields.Add(map[key]);
    }

    private static void HandleFields(List<object?> fields, string name, List<object?> newFields) {
        foreach (object? o in fields)
            if (o == null)
                newFields.Add(null);
            else if (o is IDictionary d)
                HandleKey(d, name, newFields);
            else if (o is IList l && int.TryParse(name, out int index))
                HandleIndex(l, index, newFields);
            else
                HandleField(o, name, newFields);
    }

    internal bool Check(object? root, string? rootFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields) {
        List<object?> fields = [root];
        if (fieldExpression != null) {
            if (root != null) {
                string fullName = "";
                foreach (string name in fieldExpression.Split('.')) {
                    List<object?> newFields = [];
                    fullName += name;
                    if (fullName == "*")
                        foreach (object? o in fields)
                            if (o == null)
                                newFields.Add(null);
                            else if (o is IEnumerable e)
                                foreach (object item in e)
                                    newFields.Add(item);
                            else
                                throw new Exception("Unsupported type for iteration: " + o.GetType());
                    else if (name.Length >= 3 && name[^3..^1] == "//")
                        fullName = fullName[0..^3] + fullName[^2..];
                    else if (name.Length >= 2 && name[^2] == '/') {
                        fullName = fullName[0..^2];
                        switch (char.ToUpper(name[^1])) {
                            case 'C':
                                fullName += ".";
                                continue;
                            case 'F':
                                foreach (object? o in fields)
                                    if (o == null)
                                        newFields.Add(null);
                                    else
                                        HandleField(o, fullName, newFields);
                                break;
                            case 'I':
                                foreach (object? o in fields)
                                    if (o == null)
                                        newFields.Add(null);
                                    else
                                        HandleIndex((IList)o, int.Parse(fullName), newFields);
                                break;
                            case 'K':
                                foreach (object? o in fields)
                                    if (o == null)
                                        newFields.Add(null);
                                    else
                                        HandleKey((IDictionary)o, fullName, newFields);
                                break;
                            case '*':
                                HandleFields(fields, fullName + "*", newFields);
                                break;
                            default:
                                throw new Exception("Unsupported suffix: " + name[^1]);
                        }
                    } else
                        HandleFields(fields, name, newFields);
                    fields = newFields;
                    fullName = "";
                }
            }
            if (rootFieldExpression != null)
                rootFieldExpression += "." + fieldExpression;
            else
                rootFieldExpression = fieldExpression;
        }
        HashSet<string> childPassedFields = [], childFailedFields = [];
        foreach (object? field in fields)
            if (IsFulfilledBy(field, rootFieldExpression, childPassedFields, childFailedFields) == reversed) {
                if (rootFieldExpression != null)
                    failedFields.Add(rootFieldExpression);
                if (reversed)
                    failedFields.UnionWith(childPassedFields);
                else
                    failedFields.UnionWith(childFailedFields);
                return false;
            }
        if (rootFieldExpression != null)
            passedFields.Add(rootFieldExpression);
        if (reversed)
            passedFields.UnionWith(childFailedFields);
        else
            passedFields.UnionWith(childPassedFields);
        return true;
    }

    protected abstract bool IsFulfilledBy(object? field, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields);
}