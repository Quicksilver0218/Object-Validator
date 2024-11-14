namespace Quicksilver.ObjectValidator.Runtime;
class In(bool reversed, string? fieldExpression, string?[] args) : Condition(reversed, fieldExpression)
{
    private readonly string?[] args = args;

    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        if (value == null) {
            foreach (string? arg in args)
                if (arg == null)
                    return true;
            return false;
        }
        foreach (string? arg in args)
            if (value.ToString() == arg)
                return true;
        return false;
    }
}