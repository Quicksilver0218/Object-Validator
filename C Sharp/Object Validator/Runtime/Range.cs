using System.Numerics;

namespace Quicksilver.ObjectValidator.Runtime;
class Range(bool reversed, string? fieldExpression, string range) : Condition(reversed, fieldExpression)
{
    private readonly string range = range;

    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        if (value == null)
            throw new Exception("Null values are not supported for 'range'.");
        if (!(value is sbyte || value is byte || value is short || value is ushort || value is int || value is uint || value is long || value is ulong || value is BigInteger ||
                value is float || value is double || value is decimal || value is DateTime))
            throw new Exception("Unsupported type for 'range': " + value.GetType());
        return Utils.InRange(value, range);
    }
}