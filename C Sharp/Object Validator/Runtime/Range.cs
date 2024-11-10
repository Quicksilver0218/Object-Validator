using System.Globalization;
using System.Numerics;

namespace Quicksilver.ObjectValidator.Runtime;
class Range(bool reversed, string? fieldExpression, string range) : Condition(reversed, fieldExpression)
{
    private readonly string range = range;

    protected override bool IsFulfilledBy(object? field, string? fullFieldExpression, HashSet<string> passedFields, HashSet<string> failedFields)
    {
        if (field == null)
            throw new Exception("Null values are not supported for 'range'.");
        if (field is sbyte || field is byte || field is short || field is ushort || field is int || field is uint || field is long)
            return Utils.InRange(Convert.ToInt64(field), range);
        if (field is ulong u) {
            foreach (string s in range.Split(',')) {
                string limit = s.Trim();
                if (limit.StartsWith('[')) {
                    if (u < ulong.Parse(limit[1..]))
                        return false;
                } else if (limit.StartsWith('(')) {
                    if (u <= ulong.Parse(limit[1..]))
                        return false;
                } else if (limit.EndsWith(']')) {
                    if (u > ulong.Parse(limit[..^1]))
                        return false;
                } else if (limit.EndsWith(')')) {
                    if (u >= ulong.Parse(limit[..^1]))
                        return false;
                } else if (u != ulong.Parse(limit))
                    return false;
            }
            return true;
        }
        if (field is BigInteger bi) {
            foreach (string s in range.Split(',')) {
                string limit = s.Trim();
                if (limit.StartsWith('[')) {
                    if (bi < BigInteger.Parse(limit[1..]))
                        return false;
                } else if (limit.StartsWith('(')) {
                    if (bi <= BigInteger.Parse(limit[1..]))
                        return false;
                } else if (limit.EndsWith(']')) {
                    if (bi > BigInteger.Parse(limit[..^1]))
                        return false;
                } else if (limit.EndsWith(')')) {
                    if (bi >= BigInteger.Parse(limit[..^1]))
                        return false;
                } else if (bi != BigInteger.Parse(limit))
                    return false;
            }
            return true;
        }
        if (field is float || field is double) {
            double num = Convert.ToDouble(field);
            foreach (string s in range.Split(',')) {
                string limit = s.Trim();
                if (limit.StartsWith('[')) {
                    if (num < double.Parse(limit[1..]))
                        return false;
                } else if (limit.StartsWith('(')) {
                    if (num <= double.Parse(limit[1..]))
                        return false;
                } else if (limit.EndsWith(']')) {
                    if (num > double.Parse(limit[..^1]))
                        return false;
                } else if (limit.EndsWith(')')) {
                    if (num >= double.Parse(limit[..^1]))
                        return false;
                } else if (num != double.Parse(limit))
                    return false;
            }
            return true;
        }
        if (field is decimal d) {
            foreach (string s in range.Split(',')) {
                string limit = s.Trim();
                if (limit.StartsWith('[')) {
                    if (d < decimal.Parse(limit[1..]))
                        return false;
                } else if (limit.StartsWith('(')) {
                    if (d <= decimal.Parse(limit[1..]))
                        return false;
                } else if (limit.EndsWith(']')) {
                    if (d > decimal.Parse(limit[..^1]))
                        return false;
                } else if (limit.EndsWith(')')) {
                    if (d >= decimal.Parse(limit[..^1]))
                        return false;
                } else if (d != decimal.Parse(limit))
                    return false;
            }
            return true;
        }
        if (field is DateTime dt) {
            foreach (string s in range.Split(',')) {
                string limit = s.Trim();
                if (limit.StartsWith('[')) {
                    if (dt < DateTime.Parse(limit[1..], null, DateTimeStyles.RoundtripKind))
                        return false;
                } else if (limit.StartsWith('(')) {
                    if (dt <= DateTime.Parse(limit[1..], null, DateTimeStyles.RoundtripKind))
                        return false;
                } else if (limit.EndsWith(']')) {
                    if (dt > DateTime.Parse(limit[..^1], null, DateTimeStyles.RoundtripKind))
                        return false;
                } else if (limit.EndsWith(')')) {
                    if (dt >= DateTime.Parse(limit[..^1], null, DateTimeStyles.RoundtripKind))
                        return false;
                } else if (dt != DateTime.Parse(limit, null, DateTimeStyles.RoundtripKind))
                    return false;
            }
            return true;
        }
        throw new Exception("Unsupported type for 'range': " + field.GetType());
    }
}