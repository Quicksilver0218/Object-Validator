namespace Quicksilver.ObjectValidator.Runtime;
class Rule(Config.Rule rule)
{
    internal readonly Condition condition = Utils.BuildRuntimeCondition(rule.condition);
    internal readonly int? id = rule.id;
    internal readonly string? errorMessage = rule.errorMessage;
}