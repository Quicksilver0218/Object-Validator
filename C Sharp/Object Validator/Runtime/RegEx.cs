using System.Text.RegularExpressions;

namespace Quicksilver.ObjectValidator.Runtime;
class RegEx(bool reversed, string? fieldExpression, string pattern) : Condition(reversed, fieldExpression)
{
    private readonly string pattern = pattern;

    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        if (value == null)
            throw new Exception("Null values are not supported for 'regex'.");
        if (value is string s)
            return Regex.IsMatch(s, pattern);
        throw new Exception("Unsupported type for 'regex': " + value.GetType());
    }
}