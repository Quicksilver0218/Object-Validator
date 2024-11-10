using System.Collections;

namespace Quicksilver.ObjectValidator.Runtime;
abstract class Condition(bool reversed, string? fieldExpression)
{
    protected readonly bool reversed = reversed;
    protected readonly string? fieldExpression = fieldExpression;

    internal bool Check(object? root, string? rootFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields) {
        List<object?> fields = [root];
        if (fieldExpression != null) {
            if (root != null)
                foreach (string name in fieldExpression.Split('.')) {
                    List<object?> newFields = [];
                    if (name == "*")
                        foreach (object? o in fields)
                            if (o == null)
                                newFields.Add(null);
                            else if (o is IEnumerable e)
                                foreach (object item in e)
                                    newFields.Add(item);
                            else
                                throw new Exception("Unsupported type for iteration: " + o.GetType());
                    else {
                        string newName;
                        foreach (char c in name)
                            if (c != '*') {
                                newName = name;
                                goto Handle;
                            }
                        newName = name[1..];
                        Handle:
                        foreach (object? o in fields)
                            if (o == null)
                                newFields.Add(null);
                            else if (o is IDictionary d)
                                newFields.Add(d[newName]);
                            else if (o is IList l && int.TryParse(newName, out int index)) {
                                if (index < l.Count)
                                    newFields.Add(l[index]);
                            } else
                                newFields.Add(o.GetType().GetField(newName)!.GetValue(o));
                    }
                    fields = newFields;
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