using System.Numerics;

namespace Quicksilver.ObjectValidator.Runtime;
class Utils
{
    private static bool InLimit<T>(T value, char operator_, T target, bool reversed) where T : IComparable<T> {
        int result = value.CompareTo(target);
        return operator_ switch
        {
            '<' => result < 0 ^ reversed,
            '>' => result > 0 ^ reversed,
            '=' => result == 0 ^ reversed,
            _ => throw new Exception("Unsupported operator: " + operator_),
        };
    }

    internal static bool InRange(object value, string range) {
        foreach (string str in range.Split(',')) {
            string limit = str.Trim();
            char operator_;
            string targetString;
            bool reversed;
            if (limit.StartsWith('[')) {
                operator_ = '<';
                targetString = limit[1..];
                reversed = false;
            } else if (limit.StartsWith('(')) {
                operator_ = '>';
                targetString = limit[1..];
                reversed = true;
            } else if (limit.EndsWith(']')) {
                operator_ = '>';
                targetString = limit[0..^1];
                reversed = false;
            } else if (limit.EndsWith(')')) {
                operator_ = '<';
                targetString = limit[0..^1];
                reversed = true;
            } else {
                operator_ = '=';
                targetString = limit;
                reversed = true;
            }
            switch (value) {
                case sbyte sb:
                    if (InLimit(sb, operator_, sbyte.Parse(targetString), reversed))
                        return false;
                    break;
                case byte b:
                    if (InLimit(b, operator_, byte.Parse(targetString), reversed))
                        return false;
                    break;
                case short s:
                    if (InLimit(s, operator_, short.Parse(targetString), reversed))
                        return false;
                    break;
                case ushort us:
                    if (InLimit(us, operator_, ushort.Parse(targetString), reversed))
                        return false;
                    break;
                case int i:
                    if (InLimit(i, operator_, int.Parse(targetString), reversed))
                        return false;
                    break;
                case uint ui:
                    if (InLimit(ui, operator_, uint.Parse(targetString), reversed))
                        return false;
                    break;
                case long l:
                    if (InLimit(l, operator_, long.Parse(targetString), reversed))
                        return false;
                    break;
                case ulong ul:
                    if (InLimit(ul, operator_, ulong.Parse(targetString), reversed))
                        return false;
                    break;
                case BigInteger bi:
                    if (InLimit(bi, operator_, BigInteger.Parse(targetString), reversed))
                        return false;
                    break;
                case float f:
                    if (InLimit(f, operator_, float.Parse(targetString), reversed))
                        return false;
                    break;
                case double d:
                    if (InLimit(d, operator_, double.Parse(targetString), reversed))
                        return false;
                    break;
                case decimal de:
                    if (InLimit(de, operator_, decimal.Parse(targetString), reversed))
                        return false;
                    break;
                case DateTime dt:
                    if (InLimit(dt, operator_, DateTime.Parse(targetString, null, System.Globalization.DateTimeStyles.RoundtripKind), reversed))
                        return false;
                    break;
                default:
                    throw new Exception("Unsupported type for 'inRange()': " + value.GetType());
            }
        }
        return true;
    }

    internal static Condition BuildRuntimeCondition(Config.Condition condition) {
        string t = condition.type.Trim().ToLower();
        bool reversed = false;
        while (t.StartsWith('!')) {
            reversed = !reversed;
            t = t[1..].Trim();
        }
        return t switch
        {
            "and" => new And(reversed, condition.field, condition.conditions!.Select(BuildRuntimeCondition).ToArray()),
            "or" => new Or(reversed, condition.field, condition.conditions!.Select(BuildRuntimeCondition).ToArray()),
            "null" => new Null(reversed, condition.field),
            "in" => new In(reversed, condition.field, condition.args!),
            "blank" => new Blank(reversed, condition.field),
            "regex" => new RegEx(reversed, condition.field, condition.arg!),
            "bytes" => new Bytes(reversed, condition.field, condition.arg!),
            "length" => new Length(reversed, condition.field, condition.arg!),
            "contains" => new Contains(reversed, condition.field, condition.arg!),
            "range" => new Runtime.Range(reversed, condition.field, condition.arg!),
            "true" => new True(reversed, condition.field),
            _ => throw new Exception("Unsupported condition type: " + t)
        };
    }
}