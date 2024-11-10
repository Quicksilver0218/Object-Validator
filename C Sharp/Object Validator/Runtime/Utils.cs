namespace Quicksilver.ObjectValidator.Runtime;
class Utils
{
    internal static bool InRange(long num, string range) {
        foreach (string s in range.Split(',')) {
            string limit = s.Trim();
            if (limit.StartsWith('[')) {
                if (num < long.Parse(limit[1..]))
                    return false;
            } else if (limit.StartsWith('(')) {
                if (num <= long.Parse(limit[1..]))
                    return false;
            } else if (limit.EndsWith(']')) {
                if (num > long.Parse(limit[..^1]))
                    return false;
            } else if (limit.EndsWith(')')) {
                if (num >= long.Parse(limit[..^1]))
                    return false;
            } else if (num != long.Parse(limit))
                return false;
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