import { readFileSync } from "fs";
import { join } from "path";
import rules from "./rules.json";
import { parse } from "yaml";
import { Validator, Rule } from "../src";

const validator = new Validator(rules);
class TestObject2 {
    constructor(public testDateTime: Date) {}

    toString(): string {
        const s: string = this.testDateTime.toISOString();
        return s.substring(0, s.indexOf("."));
    }
}
const testObject = {
    testString: "test測試",
    testInt: 1,
    testBool: true,
    testArray: [
        null,
        new TestObject2(new Date(Date.UTC(2023, 0, 1))),
        new TestObject2(new Date(Date.UTC(2024, 0, 1))),
    ],
    testBlankString: "   ",
    testNested: { name: "nestedValue", count: 2 },
    testMap: new Map<string, unknown>([
        ["key1", "value1"],
        ["key2", "value2"],
        ["part1.part2", "concatValue"],
        ["*", "starValue"],
        ["/A", "escapeValue"],
        ["nested", new Map<string, unknown>([["inner", "innerValue"]])],
    ]),
    testSet: new Set(["apple", "banana"]),
};
const result = validator.validate(testObject);
const failures = new Set(result.failures.map(f => f.id));
validator.fastFail = true;
const ffResult = validator.validate(testObject);

const yamlRules = parse(readFileSync(join(process.cwd(), "tests", "rules.yml"), "utf8")) as Rule[];
const yamlResult = new Validator(yamlRules).validate(testObject);
const yamlFailures = new Set(yamlResult.failures.map(f => f.id));

const scenarioRules = [
    { condition: { type: "!blank", field: "testString" }, id: 1001, errorMessage: "\"testString\" must not be blank." },
    { condition: { type: "range", field: "testInt", arg: "[0,100]" }, id: 1002 },
    { condition: { type: "true", field: "testBool" }, id: 1003 },
];
const validResult = new Validator(scenarioRules).validate({ testString: "hello", testInt: 42, testBool: true });
const invalidResult = new Validator(scenarioRules).validate({ testString: "", testInt: 999, testBool: false });
const invalidFFResult = new Validator(scenarioRules, true).validate({ testString: "", testInt: 999, testBool: false });

test("Test Null", () => {
    expect(failures.has(1)).toBe(true);
});

test("Test In", () => {
    expect(failures.has(2)).toBe(false);
});

test("Test Blank", () => {
    expect(failures.has(3)).toBe(false);
});

test("Test Regex", () => {
    expect(failures.has(4)).toBe(false);
});

test("Test Bytes", () => {
    expect(failures.has(5)).toBe(true);
});

test("Test String Length", () => {
    expect(failures.has(6)).toBe(false);
});

test("Test Array Length", () => {
    expect(failures.has(7)).toBe(false);
});

test("Test String Contains", () => {
    expect(failures.has(8)).toBe(false);
});

test("Test Array Contains", () => {
    expect(failures.has(9)).toBe(false);
});

test("Test Range", () => {
    expect(failures.has(10)).toBe(false);
});

test("Test True", () => {
    expect(failures.has(11)).toBe(false);
});

test("Test Blank Field", () => {
    expect(failures.has(12)).toBe(false);
});

test("Test Blank Field Fails", () => {
    expect(failures.has(13)).toBe(true);
});

test("Test Null Field", () => {
    expect(failures.has(14)).toBe(false);
});

test("Test Null Field Fails", () => {
    expect(failures.has(15)).toBe(true);
});

test("Test In Fails", () => {
    expect(failures.has(16)).toBe(true);
});

test("Test Regex Fails", () => {
    expect(failures.has(17)).toBe(true);
});

test("Test Bytes Range", () => {
    expect(failures.has(18)).toBe(false);
});

test("Test String Length Fails", () => {
    expect(failures.has(19)).toBe(true);
});

test("Test Map Length", () => {
    expect(failures.has(20)).toBe(false);
});

test("Test Set Length", () => {
    expect(failures.has(21)).toBe(false);
});

test("Test Set Contains", () => {
    expect(failures.has(22)).toBe(false);
});

test("Test Array Contains Fails", () => {
    expect(failures.has(23)).toBe(true);
});

test("Test Range Interval", () => {
    expect(failures.has(24)).toBe(false);
});

test("Test Range Interval Fails", () => {
    expect(failures.has(25)).toBe(true);
});

test("Test Date Range", () => {
    expect(failures.has(26)).toBe(false);
});

test("Test True Fails", () => {
    expect(failures.has(27)).toBe(true);
});

test("Test Regex Negation", () => {
    expect(failures.has(28)).toBe(false);
});

// Cases where arg / args is null (required fields being null)
test("Test In With Null Arg", () => {
    expect(failures.has(29)).toBe(false);
});

test("Test Array Contains Null Arg", () => {
    expect(failures.has(30)).toBe(false);
});

test("Test Quoted Null String Arg", () => {
    expect(failures.has(31)).toBe(false);
});

test("Test And 1", () => {
    expect(failures.has(101)).toBe(true);
});

test("Test Or 1", () => {
    expect(failures.has(102)).toBe(false);
});

test("Test And 2", () => {
    expect(failures.has(103)).toBe(true);
});

test("Test Or 2", () => {
    expect(failures.has(104)).toBe(false);
});

test("Test And Passing", () => {
    expect(failures.has(105)).toBe(false);
});

test("Test Or Fails", () => {
    expect(failures.has(106)).toBe(true);
});

test("Test Not And", () => {
    expect(failures.has(107)).toBe(true);
});

test("Test Not Or", () => {
    expect(failures.has(108)).toBe(false);
});

test("Test Nested Field", () => {
    expect(failures.has(201)).toBe(false);
});

test("Test Nested Numeric Field", () => {
    expect(failures.has(202)).toBe(false);
});

test("Test Indexed Date Time", () => {
    expect(failures.has(203)).toBe(false);
});

test("Test Map Key", () => {
    expect(failures.has(204)).toBe(false);
});

test("Test Concat Suffix", () => {
    expect(failures.has(205)).toBe(false);
});

test("Test Star Suffix", () => {
    expect(failures.has(206)).toBe(false);
});

test("Test Escape Suffix", () => {
    expect(failures.has(207)).toBe(false);
});

test("Test Key Suffix", () => {
    expect(failures.has(208)).toBe(false);
});

test("Test Index Suffix", () => {
    expect(failures.has(209)).toBe(false);
});

test("Test Field Suffix", () => {
    expect(failures.has(210)).toBe(false);
});

test("Test Nested Map Key", () => {
    expect(failures.has(211)).toBe(false);
});

test("Test Set Iteration", () => {
    expect(failures.has(212)).toBe(false);
});

test("Test Failure With Id And Message", () => {
    expect(failures.has(301)).toBe(true);
});

test("Test Failure With Message Only", () => {
    expect(result.failures.filter(f => f.id == null).length).toBe(1);
});

test("Test Passed Flag", () => {
    expect(result.passed).toBe(false);
});

test("Test Failure Count", () => {
    expect(result.failures.length).toBe(16);
});

test("Test Failure Ids", () => {
    const ids = result.failures.filter(f => f.id != null).map(f => f.id as number).sort((a, b) => a - b);
    expect(ids).toEqual([1, 5, 13, 15, 16, 17, 19, 23, 25, 27, 101, 103, 106, 107, 301]);
});

test("Test Failure Messages", () => {
    const f301 = result.failures.find(f => f.id === 301);
    expect(f301?.message).toBe("\"testString\" does not match the required pattern.");
    const fMsgOnly = result.failures.find(f => f.id == null);
    expect(fMsgOnly?.message).toBe("\"testInt\" is not in the allowed range.");
});

test("Test Failed Fields", () => {
    expect(result.failedFields.size).toBe(7);
    expect(result.failedFields.has("testString")).toBe(true);
    expect(result.failedFields.has("testArray.*")).toBe(true);
    expect(result.failedFields.has("testBlankString")).toBe(true);
    expect(result.failedFields.has("testArray.0")).toBe(true);
    expect(result.failedFields.has("testInt")).toBe(true);
    expect(result.failedFields.has("testArray")).toBe(true);
    expect(result.failedFields.has("testBool")).toBe(true);
});

test("Test Fast Fail", () => {
    expect(ffResult.passed).toBe(false);
    expect(ffResult.failures.length).toBe(1);
    expect(ffResult.failures[0].id).toBe(1);
    expect(ffResult.failedFields.size).toBe(1);
    expect(ffResult.failedFields.has("testArray.*")).toBe(true);
});

test("Test Validation Time", () => {
    const freshValidator = new Validator(rules);
    const start = Date.now();
    const freshResult = freshValidator.validate(testObject);
    console.log(Date.now() - start + " ms");
    expect(freshResult.failures.length).toBe(result.failures.length);
    expect(freshResult.failedFields.size).toBe(result.failedFields.size);
});

test("Test Valid Object", () => {
    expect(validResult.passed).toBe(true);
    expect(validResult.failures.length).toBe(0);
    expect(validResult.failedFields.size).toBe(0);
});

test("Test Invalid Object", () => {
    expect(invalidResult.passed).toBe(false);
    expect(invalidResult.failures.length).toBe(3);
    expect(invalidResult.failures.map(f => f.id).sort((a, b) => (a ?? 0) - (b ?? 0))).toEqual([1001, 1002, 1003]);
    expect(invalidResult.failedFields.size).toBe(3);
    expect(invalidResult.failedFields.has("testString")).toBe(true);
    expect(invalidResult.failedFields.has("testInt")).toBe(true);
    expect(invalidResult.failedFields.has("testBool")).toBe(true);
});

test("Test Invalid Object Fast Fail", () => {
    expect(invalidFFResult.passed).toBe(false);
    expect(invalidFFResult.failures.length).toBe(1);
    expect(invalidFFResult.failures[0].id).toBe(1001);
    expect(invalidFFResult.failures[0].message).toBe("\"testString\" must not be blank.");
    expect(invalidFFResult.failedFields.size).toBe(1);
    expect(invalidFFResult.failedFields.has("testString")).toBe(true);
});

// Tests for YAML rules.yml (corresponds to rules.json)
test("Test YAML Failure Ids", () => {
    const ids = yamlResult.failures.filter(f => f.id != null).map(f => f.id as number).sort((a, b) => a - b);
    expect(ids).toEqual([1, 5, 13, 15, 16, 17, 19, 23, 25, 27, 101, 103, 106, 107, 301]);
});

test("Test YAML In With Null Arg", () => {
    expect(yamlFailures.has(29)).toBe(false);
});

test("Test YAML Array Contains Null Arg", () => {
    expect(yamlFailures.has(30)).toBe(false);
});

test("Test YAML Quoted Null String Arg", () => {
    expect(yamlFailures.has(31)).toBe(false);
});

// An exception should be thrown when a required field is null (consistent behavior across all three languages)
function thrownBy(fn: () => unknown): unknown {
    try {
        fn();
        return undefined;
    } catch (e) {
        return e;
    }
}

test("Test Null Condition", () => {
    const rules = [{ condition: null }] as unknown as Rule[];
    expect(thrownBy(() => new Validator(rules))).toBe("Required rule field 'condition' is null.");
});

test("Test Null Type", () => {
    const rules = [{ condition: { type: null, field: "testString" } }] as unknown as Rule[];
    expect(thrownBy(() => new Validator(rules))).toBe("Required condition field 'type' is null.");
});

test("Test In Null Args", () => {
    const rules = [{ condition: { type: "in", field: "testInt", args: null } }] as unknown as Rule[];
    expect(thrownBy(() => new Validator(rules))).toBe("Required condition field 'args' is null for type 'in'.");
});

test("Test And Null Conditions", () => {
    const rules = [{ condition: { type: "and", conditions: null } }] as unknown as Rule[];
    expect(thrownBy(() => new Validator(rules))).toBe("Required condition field 'conditions' is null for type 'and'.");
});

test("Test Or Null Conditions", () => {
    const rules = [{ condition: { type: "or", conditions: null } }] as unknown as Rule[];
    expect(thrownBy(() => new Validator(rules))).toBe("Required condition field 'conditions' is null for type 'or'.");
});

test("Test Regex Null Arg", () => {
    const rules = [{ condition: { type: "regex", field: "testString", arg: null } }];
    expect(thrownBy(() => new Validator(rules))).toBe("Required condition field 'arg' is null for type 'regex'.");
});

test("Test Bytes Null Arg", () => {
    const rules = [{ condition: { type: "bytes", field: "testString", arg: null } }];
    expect(thrownBy(() => new Validator(rules))).toBe("Required condition field 'arg' is null for type 'bytes'.");
});

test("Test Length Null Arg", () => {
    const rules = [{ condition: { type: "length", field: "testString", arg: null } }];
    expect(thrownBy(() => new Validator(rules))).toBe("Required condition field 'arg' is null for type 'length'.");
});

test("Test Range Null Arg", () => {
    const rules = [{ condition: { type: "range", field: "testInt", arg: null } }];
    expect(thrownBy(() => new Validator(rules))).toBe("Required condition field 'arg' is null for type 'range'.");
});

test("Test Contains Null Arg String", () => {
    const v = new Validator([{ condition: { type: "contains", field: "testString", arg: null } }]);
    expect(thrownBy(() => v.validate(testObject))).toBe("Null argument is not supported for 'contains' with string values.");
});

test("Test Contains Null Arg Iterable", () => {
    const v = new Validator([{ condition: { type: "contains", field: "testArray", arg: null } }]);
    expect(v.validate(testObject).passed).toBe(true);
});
