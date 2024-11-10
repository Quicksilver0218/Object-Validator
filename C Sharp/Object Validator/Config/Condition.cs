namespace Quicksilver.ObjectValidator.Config;
public class Condition(string type, string? field, string? arg, string?[]? args, Condition[]? conditions)
{
    public readonly string type = type;
    public readonly string? field = field, arg = arg;
    public readonly string?[]? args = args;
    public readonly Condition[]? conditions = conditions;
}