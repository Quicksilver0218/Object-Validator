using System.Text;

namespace Quicksilver.ObjectValidator.Runtime;
class Bytes(bool reversed, string? fieldExpression, string range) : Condition(reversed, fieldExpression)
{
    private readonly string range = range;

    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        return value switch {
            null => throw new Exception("Null values are not supported for 'bytes'."),
            string s => Utils.InRange(Encoding.UTF8.GetByteCount(s), range),
            _ => throw new Exception("Unsupported type for 'bytes': " + value.GetType())
        };
    }
}