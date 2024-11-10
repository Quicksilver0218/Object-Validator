using System.Collections;

namespace Quicksilver.ObjectValidator.Runtime;
class Length(bool reversed, string? fieldExpression, string range) : Condition(reversed, fieldExpression)
{
    private readonly string range = range;

    protected override bool IsFulfilledBy(object? field, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        if (field == null)
            throw new Exception("Null values are not supported for 'length'.");
        if (field is string s)
            return Utils.InRange(s.Length, range);
        if (field is ICollection c)
            return Utils.InRange(c.Count, range);
        throw new Exception("Unsupported type for 'length': " + field.GetType());
    }
}