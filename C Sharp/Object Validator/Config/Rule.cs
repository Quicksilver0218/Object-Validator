namespace Quicksilver.ObjectValidator.Config;
public class Rule(Condition condition, int? id = null, string? errorMessage = null)
{
    public readonly Condition condition = condition;
    public readonly int? id = id;
    public readonly string? errorMessage = errorMessage;
}