namespace Quicksilver.ObjectValidator.Runtime;
class Blank(bool reversed, string? fieldExpression) : Condition(reversed, fieldExpression)
{
    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        return value switch {
            null => throw new Exception("Null values are not supported for 'blank'."),
            string s => string.IsNullOrWhiteSpace(s),
            _ => throw new Exception("Unsupported type for 'blank': " + value.GetType())
        };
    }
}