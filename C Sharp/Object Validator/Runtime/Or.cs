namespace Quicksilver.ObjectValidator.Runtime;
class Or(bool reversed, string? fieldExpression, Condition[] conditions) : Condition(reversed, fieldExpression)
{
    protected readonly Condition[] conditions = conditions;

    protected override bool IsFulfilledBy(object? field, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        HashSet<string> newFailedFields = [];
        foreach (Condition condition in conditions)
            if (condition.Check(field, fullFieldExpression, passedFields, newFailedFields))
                return true;
        failedFields.UnionWith(newFailedFields);
        return false;
    }
}