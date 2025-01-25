# Object-Validator [![NuGet](https://img.shields.io/nuget/v/Quicksilver0218.ObjectValidator)](https://www.nuget.org/packages/Quicksilver0218.ObjectValidator) [![Maven](https://img.shields.io/maven-central/v/io.github.quicksilver0218/com.quicksilver.objectvalidator)](https://central.sonatype.com/artifact/io.github.quicksilver0218/com.quicksilver.objectvalidator) [![npm](https://img.shields.io/npm/v/%40quicksilver0218%2Fobject-validator)](https://www.npmjs.com/package/@quicksilver0218/object-validator)
A library providing functions that let users validate the values in objects with YAML-formatted rules.

## Installation
[NuGet Gallery](https://www.nuget.org/packages/Quicksilver0218.ObjectValidator)

[Maven Central Repository](https://central.sonatype.com/artifact/io.github.quicksilver0218/com.quicksilver.objectvalidator)

[npm Public Registry](https://www.npmjs.com/package/@quicksilver0218/object-validator)

## Usage
[Document](Document.md)
### JSON / YAML Rule List
First of all, a list of rules in JSON / YAML format should be provided. Here is the structure of the list:
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

### Program Code
#### C♯
```cs
using Quicksilver.ObjectValidator;

Validator validator = new(File.OpenText("path/validation-rules.json"));
ValidationResult validationResult = validator.Validate(myObject);
```

#### Java
```java
import com.quicksilver.objectvalidator.Validator;
// ...
Validator validator = new Validator(getClass().getResource("/path/validation-rules.json"));
ValidationResult validationResult = validator.Validate(myObject);
```

#### JavaScript / TypeScript
```js
import rules from "path/validation-rules.json";
import { Validator } from "@quicksilver0218/object-validator";

const validator = new Validator(rules);
const validationResult = validator.validate(myObject);
```
