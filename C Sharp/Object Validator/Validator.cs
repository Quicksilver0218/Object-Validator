using System.Text.Json;
using Quicksilver.ObjectValidator.Runtime;

namespace Quicksilver.ObjectValidator;
public class Validator(Config.Rule[] rules, bool fastFail = false)
{
    private static readonly JsonSerializerOptions options = new() { IncludeFields = true };
    private readonly Rule[] rules = rules.Select(r => new Rule(r)).ToArray();
    public bool fastFail = fastFail;

    public Validator(string rulesJson, bool fastFail = false) : this(JsonSerializer.Deserialize<Config.Rule[]>(rulesJson, options)!, fastFail) {}

    public ValidationResult Validate(object? obj)
    {
        bool valid = true;
        HashSet<string> failedFields = [];
        List<ValidationFailure> failures = [];
        foreach (Rule rule in rules) {
            if (!rule.condition.Check(obj, null, [], failedFields)) {
                valid = false;
                if (rule.id != null || rule.errorMessage != null)
                    failures.Add(new(rule.id, rule.errorMessage));
                if (fastFail)
                    break;
            }
        }
        return new(valid, failedFields, failures);
    }
}