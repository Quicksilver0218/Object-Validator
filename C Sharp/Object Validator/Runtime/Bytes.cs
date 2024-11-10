using System.Text;

namespace Quicksilver.ObjectValidator.Runtime;
class Bytes(bool reversed, string? fieldExpression, string range) : Condition(reversed, fieldExpression)
{
    private readonly string range = range;

    protected override bool IsFulfilledBy(object? field, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        if (field == null)
            throw new Exception("Null values are not supported for 'bytes'.");
        if (field is string s)
            return Utils.InRange(Encoding.UTF8.GetByteCount(s), range);
        throw new Exception("Unsupported type for 'bytes': " + field.GetType());
    }
}