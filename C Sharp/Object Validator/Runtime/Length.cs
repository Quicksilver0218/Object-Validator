using System.Collections;

namespace Quicksilver.ObjectValidator.Runtime;
class Length(bool reversed, string? fieldExpression, string range) : Condition(reversed, fieldExpression)
{
    private readonly string range = range;

    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        return value switch {
            null => throw new Exception("Null values are not supported for 'length'."),
            string s => Utils.InRange(s.Length, range),
            ICollection c => Utils.InRange(c.Count, range),
            IReadOnlyCollection<object> roc => Utils.InRange(roc.Count, range),
            _ => throw new Exception("Unsupported type for 'length': " + value.GetType())
        };
    }
}