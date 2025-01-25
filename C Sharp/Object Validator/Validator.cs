using Quicksilver.ObjectValidator.Runtime;
using YamlDotNet.Serialization;

namespace Quicksilver.ObjectValidator;
public class Validator(Config.Rule[] rules, bool fastFail = false)
{
    private static readonly IDeserializer deserializer = new DeserializerBuilder().WithTypeConverter(new TypeConverter()).Build();
    private readonly Rule[] rules = [..rules.Select(r => new Rule(r))];
    public bool fastFail = fastFail;

    public Validator(string rulesYaml, bool fastFail = false) : this(deserializer.Deserialize<Config.Rule[]>(rulesYaml)!, fastFail) {}

    public Validator(TextReader reader, bool fastFail = false) : this(deserializer.Deserialize<Config.Rule[]>(reader)!, fastFail) {}

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