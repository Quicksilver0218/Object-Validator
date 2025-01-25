# Table of Contents
- [JSON / YAML Rule List](#json--yaml-rule-list)
    - [Field Expression](#field-expression)
        - [General](#general)
        - [Iterable](#iterable)
        - [Type Precedence and Suffixes](#type-precedence-and-suffixes)
            - [Type Precedence](#type-precedence)
            - [Suffixes](#suffixes)
    - [Condition Types](#condition-types)
        - [Not (!)](#not-)
        - [And](#and)
        - [Or](#or)
        - [Null](#null)
        - [In](#in)
        - [Blank](#blank)
        - [RegEx](#regex)
        - [Bytes](#bytes)
        - [Length](#length)
        - [Contains](#contains)
        - [Range](#range)
        - [True](#true)
    - [Failure Identification](#failure-identification)
- [Classes / Types](#classes--types)
    - [C♯](#c)
        - [Validator](#validator)
        - [ValidationResult](#validationresult)
        - [ValidationFailure](#validationfailure)
        - [Rule](#rule)
        - [Condition](#condition)
    - [Java](#java)
        - [Validator](#validator-1)
        - [ValidationResult](#validationresult-1)
        - [ValidationFailure](#validationfailure-1)
        - [Rule](#rule-1)
        - [Condition](#condition-1)
    - [JavaScript / TypeScript](#javascript--typescript)
        - [Validator](#validator-2)
        - [ValidationResult](#validationresult-2)
        - [ValidationFailure](#validationfailure-2)
        - [Rule](#rule-2)
        - [Condition](#condition-2)

## JSON / YAML Rule List
Structure of the rule list:

```ts
type Root = {
    condition: Condition;
    id?: number;
    errorMessage?: string;
}[]

type Condition = {
    type: string;
    field?: string;
    arg?: string | null;
    args?: (string | null)[];
    conditions?: Condition[];
}
```

[JSON Schema](validation-rules-schema.json)

For example:
```json
[
    {
        "condition": {
            "type": "and",
            "field": "name",
            "conditions": [
                {
                    "type": "!null"
                },
                {
                    "type": "!blank"
                }
            ]
        },
        "id": 1,
        "errorMessage": "\"name\" is required."
    },
    {
        "condition": {
            "type": "range",
            "field": "age",
            "arg": "[18"
        },
        "id": 2,
        "errorMessage": "\"age\" should be >= 18."
    }
]
```

### Field Expression
#### General
Consider this object:

```json
{
    "name": "John Smith",
    "otherName": null,
    "age": 25,
    "address": 
    {
        "city": "New York",
        "state": "NY"
    }
}
```

A field can be selected by putting its name into the expression. e.g. `name`, `otherName`, `age`, `address`. Nested fields can be selected by using dot separated expressions. e.g. `address.city`, `address.state`.

For maps (dictionaries), the field names are treated as keys. i.e. When `address` is a map that contains a field named `size`, that field can never be selected by `address.size`. Instead, the value of the entry (key-value pair) with the key `size` will be selected.

In C♯ and Java, undeclared field names are valid for `null` but invalid for non-null values. e.g. `otherName.city` returns `null` while `name.city` throws an exception. In JavaScript / TypeScript, both of them are valid but the returned values depend on the values. e.g. `otherName.city` returns `null` while `name.city` returns `undefined`.

Field expressions are nested. For example, this is a valid rule for the above object:

```json
{
    "condition": {
        "type": "and",
        "field": "address",
        "conditions": [
            {
                "type": "!null"
            },
            {
                "type": "!null",
                "field": "city"
            }
        ]
    }
}
```

#### Iterable
For iterable objects (e.g. `Set`), iteration can be performed by using `*`. Consider this object:

```json
{
    "name": "John Smith",
    "phoneNumber": 
    [
        {
            "type": "home",
            "number": "212 555-1234"
        },
        {
            "type": "fax",
            "number": "646 555-4567"
        }
    ]
}
```

`phoneNumber.*` can be used to iterate over `phoneNumber`. For example, this rule will be passed only when all the phone numbers are not `null`:

```json
{
    "condition": {
        "type": "!null",
        "field": "phoneNumber.*.number"
    }
}
```

If you want to use `*` as a key in a `Map` (`Dictionary`), please put `/*` or `*/K` into the expression instead. (Please see [Suffixes](#suffixes) for detail.)

For lists and arrays, indices can also be used. e.g. `phoneNumber.0.type`. While using an index out of bounds throws an exception in C♯ and Java, it is valid and `undefined` is returned in JavaScript / TypeScript.

#### Type Precedence and Suffixes
##### Type Precedence
By default, a field name will be handled in this order:
1. If the upper-level object is a `Map` (`Dictionary`), the field name will be treated as a key.
2. If the upper-level object is a `List` / `Array` and the field name can be parsed to an integer, the field name will be treated as an index.
3. The field name will be treated as a field name.

Therefore, if you want to express a field of a `Map` (`Dictionary`), a [Suffix](#suffixes) is required.

##### Suffixes
Field names ending with `/?` will be treated as having a suffix, where `?` can be any character. Suffixes are case-insensitive. Supported suffixes are listed below.

###### C
`C` stands for 'Concat'. This should be used when you want to have `.` in a field name. e.g. `part1/C.part2` represents `part1.part2` as a whole field name.

###### F
`F` stands for 'Field'. Field names with this suffix will be forcefully treated as a field name.

###### I
`I` stands for 'Index'. Field names with this suffix will be forcefully treated as an index in a `List` / `Array`.

###### K
`K` stands for 'Key'. Field names with this suffix will be forcefully treated as a key in a `Map` (`Dictionary`).

###### *
This can be used when you want to use `*` as a field name. e.g. `/*` represents `*` as a field name.

###### Escape Character (/)
Sometimes a field name without suffixes looks like having a suffix. You can prevent a field name such as this from being treated as having a suffix by adding one more `/` before `/`. e.g. `//A` represents `/A` as a field name.

### Condition Types
#### Not (!)
A NOT gate can be applied to a condition by adding `!` to the beginning. e.g. `!null`.

#### And
`and` represents a AND gate. The `conditions` field is required for this condition type. The condition is passed when all of the conditions are passed.

#### Or
`or` represents a OR gate. The `conditions` field is required for this condition type. The condition is passed when any of the conditions are passed.

#### Null
`null` checks whether the value is a null value. The condition is passed when the value is `null` or `undefined`.

#### In
`in` checks whether the string representation of the value exists in `args`. The string representation is obtained by calling `toString()`/`ToString()`. The `args` field is required for this condition type. The condition is passed when the string representation of the value exists in `args`.

#### Blank
`blank` checks whether the value is blank. It only supports string values. The condition is passed when the value is empty or contains only white space characters.

#### Regex
`regex` checks whether the value matches the given regular expression. It only supports string values. The `arg` field is required as the regular expression for this condition type. The condition is passed when the value matches the given regular expression.

#### Bytes
`bytes` checks whether the length of the value in UTF-8 encoding in bytes is within the given range. It only supports string values. The `arg` field is required as the range expression for this condition type. (Please see [Range Expression](#range-expression) for detail.) The condition is passed when the length of the value in UTF-8 encoding in bytes is within the given range.

#### Length
`length` checks whether the length of the value is within the given range. It supports string-like, array-like, `Map` (`Dictionary`) and `Set` values. The `arg` field is required as the range expression for this condition type. (Please see [Range Expression](#range-expression) for detail.) The condition is passed when the length of the value is within the given range.

#### Contains
`contains` checks whether the value contains the given string or null value. It supports string and iterable values. The `arg` field is required as the given string or null value. For string values, the condition is passed when the value contains the given string. For iterable values, the condition is passed when any of the string representation of the elements equals to the given string or null value. The string representation is obtained by calling `toString()`/`ToString()`.

#### Range
`range` checks whether the value is within the given range. It supports number and date (C♯: [DateTime](https://learn.microsoft.com/en-us/dotnet/api/system.datetime), Java: [Date](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Date.html), JavaScript: [Date](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date)) values. The `arg` field is required as the range expression for this condition type. ISO 8601 format is used for date. (Please see [Range Expression](#range-expression) for detail.) The condition is passed when the value is within the given range.

##### Range Expression
Ranges should be expressed as [intervals](https://en.wikipedia.org/wiki/Interval_(mathematics)). e.g. `[0, 1)`, `[2020-01-01T00:00:00Z`. Equality checks can be done without brackets. e.g. `100`.

#### True
`true` checks whether the value is `true`. It only supports boolean values. The condition is passed when the value is `true`.

### Failure Identification
After validation, you can know whether the data is valid. However, to know that which rules are violated, extra information (i.e. `id`, `errorMessage`) are needed to be added to the rules. When a rule with `id` or `errorMessage` is violated, a `ValidationFailure` with the same `id` and `message` will be added to `failures` of the `ValidationResult`. (Please see [Classes / Types](#classes--types) for detail.)

## Classes / Types
### C♯
#### Validator
```cs
namespace Quicksilver.ObjectValidator;
```

##### Constructors
```cs
public Validator(Rule[] rules, bool fastFail = false);
public Validator(string rulesYaml, bool fastFail = false);
public Validator(TextReader reader, bool fastFail = false);
```

##### Fields
```cs
public bool fastFail;
```

##### Methods
```cs
public ValidationResult Validate(object? obj);
```

#### ValidationResult
```cs
namespace Quicksilver.ObjectValidator;
```

##### Fields
```cs
public readonly bool passed;
public readonly ISet<string> failedFields;
public readonly ICollection<ValidationFailure> failures;
```

#### ValidationFailure
```cs
namespace Quicksilver.ObjectValidator;
```

##### Fields
```cs
public readonly int? id;
public readonly string? message;
```

#### Rule
```cs
namespace Quicksilver.ObjectValidator.Config;
```

##### Constructors
```cs
public Rule(Condition condition, int? id = null, string? errorMessage = null);
```

##### Fields
```cs
public readonly Condition condition;
public readonly int? id;
public readonly string? errorMessage;
```

#### Condition
```cs
namespace Quicksilver.ObjectValidator.Config;
```

##### Constructors
```cs
public Condition(string type, string? field, string? arg, string?[]? args, Condition[]? conditions);
```

##### Fields
```cs
public readonly string type;
public readonly string? field;
public readonly string? arg;
public readonly string?[]? args;
public readonly Condition[]? conditions;
```

### Java
#### Validator
```java
package com.quicksilver.objectvalidator;
```

##### Constructors
```java
public Validator(Rule[] rules, boolean fastFail = false);
public Validator(String rulesYaml, boolean fastFail = false) throws JsonMappingException, JsonProcessingException;
public Validator(URL url, boolean fastFail = false) throws IOException;
public Validator(InputStream stream, boolean fastFail = false) throws IOException;
```

##### Fields
```java
public boolean fastFail;
```

##### Methods
```java
public ValidationResult validate(Object obj) throws ReflectiveOperationException;
```

#### ValidationResult
```java
package com.quicksilver.objectvalidator;
```

##### Fields
```java
public final boolean passed;
public final Set<String> failedFields;
public final Collection<ValidationFailure> failures;
```

#### ValidationFailure
```java
package com.quicksilver.objectvalidator;
```

##### Fields
```java
public final Integer id;
public final String message;
```

#### Rule
```java
package com.quicksilver.objectvalidator.config;
```

##### Constructors
```java
public Rule(Condition condition, Integer id, String errorMessage);
```

##### Fields
```java
public final Condition condition;
public final Integer id;
public final String errorMessage;
```

#### Condition
```java
package com.quicksilver.objectvalidator.config;
```

##### Constructors
```java
public Condition(String type, String field, String arg, String[] args, Condition[] conditions);
```

##### Fields
```java
public final String type;
public final String field;
public final String arg;
public final String[] args;
public final Condition[] conditions;
```

### JavaScript / TypeScript
#### Validator
```ts
module "@quicksilver0218/object-validator";
```

##### Constructors
```ts
constructor(rules: Rule[], fastFail = false);
```

##### Fields
```ts
fastFail: boolean;
```

##### Methods
```ts
validate(obj: any): ValidationResult;
```

#### ValidationResult
```ts
module "@quicksilver0218/object-validator";
```

##### Fields
```ts
readonly passed: boolean;
readonly failedFields: Set<string>;
readonly failures: ValidationFailure[];
```

#### ValidationFailure
```ts
module "@quicksilver0218/object-validator";
```

##### Fields
```ts
readonly id?: number;
readonly message?: string;
```

#### Rule
```ts
module "@quicksilver0218/object-validator";
```

##### Fields
```ts
readonly condition: Condition;
readonly id?: number;
readonly errorMessage?: string;
```

#### Condition
```ts
module "@quicksilver0218/object-validator";
```

##### Fields
```ts
readonly type: string;
readonly field?: string;
readonly arg?: string | null;
readonly args?: (string | null)[];
readonly conditions?: Condition[];
```
