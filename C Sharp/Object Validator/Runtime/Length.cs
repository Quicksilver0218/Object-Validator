using System.Collections;

namespace Quicksilver.ObjectValidator.Runtime;
class Length(bool reversed, string? fieldExpression, string range) : Condition(reversed, fieldExpression)
{
    private readonly string range = range;

    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        if (value == null)
            throw new Exception("Null values are not supported for 'length'.");
        if (value is string s)
            return Utils.InRange(s.Length, range);
        if (value is ICollection c)
            return Utils.InRange(c.Count, range);
        throw new Exception("Unsupported type for 'length': " + value.GetType());
    }
}