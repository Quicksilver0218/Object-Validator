namespace Quicksilver.ObjectValidator.Runtime;
class Or(bool reversed, string? fieldExpression, Condition[] conditions) : Condition(reversed, fieldExpression)
{
    protected readonly Condition[] conditions = conditions;

    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        HashSet<string> newFailedFields = [];
        foreach (Condition condition in conditions)
            if (condition.Check(value, fullFieldExpression, passedFields, newFailedFields))
                return true;
        failedFields.UnionWith(newFailedFields);
        return false;
    }
}