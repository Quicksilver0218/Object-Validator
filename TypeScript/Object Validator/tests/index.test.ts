import rules from "./rules.json";
import { Validator } from "../src";

const validator = new Validator(rules);
function TestObject2(date: Date) {
    this.testDateTime = date;
}
TestObject2.prototype.toString = function() {
    const s: string = this.testDateTime.toISOString();
    return s.substring(0, s.indexOf("."));
};
const testObject = {
    testString: "test測試",
    testInt: 1,
    testBool: true,
    testArray: [
        null,
        new TestObject2(new Date(Date.UTC(2023, 0, 1))),
        new TestObject2(new Date(Date.UTC(2024, 0, 1))),
    ]
};
const start = Date.now();
const result = validator.validate(testObject);
console.log(Date.now() - start + " ms");
const failures = new Set(result.failures.map(f => f.id));
validator.fastFail = true;
const ffResult = validator.validate(testObject);

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

test("Test Failed Fields", () => {
    expect(result.failedFields.size).toBe(2);
    expect(result.failedFields.has("testString")).toBe(true);
    expect(result.failedFields.has("testArray.*")).toBe(true);
});

test("Test Fast Fail", () => {
    expect(ffResult.failures.length).toBe(1);
});