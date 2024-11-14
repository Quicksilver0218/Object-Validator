namespace Quicksilver.ObjectValidator.Runtime;
class True(bool reversed, string? fieldExpression) : Condition(reversed, fieldExpression)
{
    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        return value is true;
    }
}