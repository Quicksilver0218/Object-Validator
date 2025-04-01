using Quicksilver.ObjectValidator.Config;
using YamlDotNet.Core;
using YamlDotNet.Core.Events;
using YamlDotNet.Serialization;

namespace Quicksilver.ObjectValidator;
internal sealed class TypeConverter : IYamlTypeConverter
{
    public bool Accepts(Type type)
    {
        return type == typeof(Rule[]);
    }

    public object? ReadYaml(IParser parser, Type type, ObjectDeserializer rootDeserializer)
    {
        int state = 0;
        List<Rule> rules = [];
        do {
            switch (state) {
                case 0:
                    if (parser.Current is not SequenceStart)
                        throw new YamlException("Expected SequenceStart");
                    state = 1;
                    break;
                case 1:
                    switch (parser.Current) {
                        case SequenceEnd:
                            parser.MoveNext();
                            if (parser.Current is not DocumentEnd)
                                throw new YamlException("Expected DocumentEnd");
                            return rules.ToArray();
                        case MappingStart:
                            state = 2;
                            break;
                        default:
                            throw new YamlException("Expected MappingStart or SequenceEnd");
                    }
                    break;
                case 2:
                    Condition? condition = null;
                    int? id = null;
                    string? message = null;
                    while (parser.Current is not MappingEnd) {
                        string key = (parser.Current as Scalar)!.Value;
                        parser.MoveNext();
                        switch (key) {
                            case "condition":
                                condition = ReadCondition(parser);
                                break;
                            case "id":
                                id = int.Parse((parser.Current as Scalar)!.Value);
                                break;
                            case "message":
                                message = (parser.Current as Scalar)!.Value;
                                break;
                        }
                        parser.MoveNext();
                    }
                    rules.Add(new Rule(condition!, id, message));
                    state = 1;
                    break;
            }
        } while (parser.MoveNext());
        throw new YamlException("Unexpected end of document");
    }

    private static Condition ReadCondition(IParser parser)
    {
        int state = 0;
        do {
            switch (state) {
                case 0:
                    if (parser.Current is not MappingStart)
                        throw new YamlException("Expected MappingStart");
                    state = 1;
                    break;
                case 1:
                    string? type = null, field = null, arg = null;
                    string?[]? args = null;
                    Condition[]? conditions = null;
                    while (parser.Current is not MappingEnd) {
                        string key = (parser.Current as Scalar)!.Value;
                        parser.MoveNext();
                        switch (key) {
                            case "type":
                                type = (parser.Current as Scalar)!.Value;
                                break;
                            case "field":
                                field = (parser.Current as Scalar)!.Value;
                                break;
                            case "arg":
                                arg = (parser.Current as Scalar)!.Value;
                                break;
                            case "args":
                                if (parser.Current is not SequenceStart)
                                    throw new YamlException("Expected SequenceStart");
                                parser.MoveNext();
                                List<string> argList = [];
                                while (parser.Current is not SequenceEnd) {
                                    argList.Add((parser.Current as Scalar)!.Value);
                                    parser.MoveNext();
                                }
                                args = [..argList];
                                break;
                            case "conditions":
                                if (parser.Current is not SequenceStart)
                                    throw new YamlException("Expected SequenceStart");
                                parser.MoveNext();
                                List<Condition> conditionList = [];
                                while (parser.Current is not SequenceEnd) {
                                    conditionList.Add(ReadCondition(parser));
                                    parser.MoveNext();
                                }
                                conditions = [..conditionList];
                                break;
                        }
                        parser.MoveNext();
                    }
                    return new Condition(type!, field, arg, args, conditions);
            }
        } while (parser.MoveNext());
        throw new YamlException("Unexpected end of document");
    }

    public void WriteYaml(IEmitter emitter, object? value, Type type, ObjectSerializer serializer)
    {
        throw new NotImplementedException();
    }
}