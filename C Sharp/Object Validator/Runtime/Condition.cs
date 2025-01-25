using System.Collections;
using System.Reflection;

namespace Quicksilver.ObjectValidator.Runtime;
abstract class Condition(bool reversed, string? fieldExpression)
{
    protected readonly bool reversed = reversed;
    protected readonly string? fieldExpression = fieldExpression;

    private static void HandleField(object obj, string field, List<object?> values) {
        values.Add(obj.GetType().GetField(field, BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance)!.GetValue(obj));
    }

    private static void HandleIndex(IList list, int index, List<object?> values) {
        values.Add(list[index]);
    }

    private static void HandleKey(IDictionary map, string key, List<object?> values) {
        values.Add(map[key]);
    }

    private static void HandleValues(List<object?> values, string name, List<object?> newValues) {
        foreach (object? value in values)
            if (value == null)
                newValues.Add(null);
            else if (value is IDictionary d)
                HandleKey(d, name, newValues);
            else if (value is IList l && int.TryParse(name, out int index))
                HandleIndex(l, index, newValues);
            else
                HandleField(value, name, newValues);
    }

    internal bool Check(object? root, string? rootFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields) {
        List<object?> values = [root];
        if (fieldExpression != null) {
            if (root != null) {
                string fullName = "";
                foreach (string name in fieldExpression.Split('.')) {
                    List<object?> newValues = [];
                    fullName += name;
                    if (fullName == "*")
                        foreach (object? o in values)
                            if (o == null)
                                newValues.Add(null);
                            else if (o is IEnumerable e)
                                foreach (object item in e)
                                    newValues.Add(item);
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
                                foreach (object? o in values)
                                    if (o == null)
                                        newValues.Add(null);
                                    else
                                        HandleField(o, fullName, newValues);
                                break;
                            case 'I':
                                foreach (object? o in values)
                                    if (o == null)
                                        newValues.Add(null);
                                    else
                                        HandleIndex((IList)o, int.Parse(fullName), newValues);
                                break;
                            case 'K':
                                foreach (object? o in values)
                                    if (o == null)
                                        newValues.Add(null);
                                    else
                                        HandleKey((IDictionary)o, fullName, newValues);
                                break;
                            case '*':
                                HandleValues(values, fullName + "*", newValues);
                                break;
                            default:
                                throw new Exception("Unsupported suffix: " + name[^1]);
                        }
                    } else
                        HandleValues(values, name, newValues);
                    values = newValues;
                    fullName = "";
                }
            }
            if (rootFieldExpression != null)
                rootFieldExpression += "." + fieldExpression;
            else
                rootFieldExpression = fieldExpression;
        }
        HashSet<string> childPassedFields = [], childFailedFields = [];
        foreach (object? value in values)
            if (IsFulfilledBy(value, rootFieldExpression, childPassedFields, childFailedFields) == reversed) {
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

    protected abstract bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields);
}