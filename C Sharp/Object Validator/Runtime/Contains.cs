using System.Collections;

namespace Quicksilver.ObjectValidator.Runtime;
class Contains(bool reversed, string? fieldExpression, string? arg) : Condition(reversed, fieldExpression)
{
    private readonly string? arg = arg;

    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        switch (value) {
            case null:
                throw new Exception("Null values are not supported for 'contains'.");
            case string s:
                return s.Contains(arg!);
            case IEnumerable e:
                foreach (object o in e)
                    if (o == null) {
                        if (arg == null)
                            return true;
                    } else if (o.ToString() == arg)
                        return true;
                return false;
            default:
                throw new Exception("Unsupported type for 'contains': " + value.GetType());
        }
    }
}