namespace Quicksilver.ObjectValidator.Runtime;
class Blank(bool reversed, string? fieldExpression) : Condition(reversed, fieldExpression)
{
    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        if (value == null)
            throw new Exception("Null values are not supported for 'blank'.");
        if (value is string s)
            return string.IsNullOrWhiteSpace(s);
        throw new Exception("Unsupported type for 'blank': " + value.GetType());
    }
}