using System.Numerics;

namespace Quicksilver.ObjectValidator.Runtime;
class Range(bool reversed, string? fieldExpression, string range) : Condition(reversed, fieldExpression)
{
    private readonly string range = range;

    protected override bool IsFulfilledBy(object? value, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        return value switch
        {
            null => throw new Exception("Null values are not supported for 'range'."),
            sbyte or byte or short or ushort or int or uint or long or ulong or BigInteger or float or double or decimal or DateTime => Utils.InRange(value, range),
            _ => throw new Exception("Unsupported type for 'range': " + value.GetType()),
        };
    }
}