namespace Quicksilver.ObjectValidator.Runtime;
class And(bool reversed, string? fieldExpression, Condition[] conditions) : Condition(reversed, fieldExpression)
{
    protected readonly Condition[] conditions = conditions;

    protected override bool IsFulfilledBy(object? field, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        HashSet<string> newPassedFields = [];
        foreach (Condition condition in conditions)
            if (!condition.Check(field, fullFieldExpression, newPassedFields, failedFields))
                return false;
        passedFields.UnionWith(newPassedFields);
        return true;
    }
}