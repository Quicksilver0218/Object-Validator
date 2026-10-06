using System.Diagnostics;
using Quicksilver.ObjectValidator;
using Quicksilver.ObjectValidator.Config;

namespace ObjectValidatorTest;
[TestClass]
public class LibraryTest
{
    private readonly Validator validator;
    private readonly TestObject testObject;
    private readonly ValidationResult result, ffResult;
    private readonly HashSet<int?> failures;
    private readonly ValidationResult validResult, invalidResult, invalidFFResult;

    public LibraryTest()
    {
        validator = new(File.OpenText("rules.json"));
        testObject = new(
            "test測試",
            1,
            true,
            [
                null,
                new(new(2023, 1, 1)),
                new(new(2024, 1, 1))
            ],
            "   ",
            new("nestedValue", 2),
            new() { ["key1"] = "value1", ["key2"] = "value2", ["part1.part2"] = "concatValue", ["*"] = "starValue", ["/A"] = "escapeValue", ["nested"] = new Dictionary<string, object?> { ["inner"] = "innerValue" } },
            new HashSet<string> { "apple", "banana" }
        );
        result = validator.Validate(testObject);
        failures = result.failures.Select(f => f.id).ToHashSet();
        validator.fastFail = true;
        ffResult = validator.Validate(testObject);

        Rule[] scenarioRules =
        [
            new(new Condition("!blank", "testString", null, null, null), 1001, "\"testString\" must not be blank."),
            new(new Condition("range", "testInt", "[0,100]", null, null), 1002, null),
            new(new Condition("true", "testBool", null, null, null), 1003, null)
        ];
        TestObject validObject = new("hello", 42, true, [], "value", new("nestedValue", 2), new(), new());
        TestObject invalidObject = new("", 999, false, [], "  ", new("nestedValue", 2), new(), new());
        validResult = new Validator(scenarioRules).Validate(validObject);
        invalidResult = new Validator(scenarioRules).Validate(invalidObject);
        invalidFFResult = new Validator(scenarioRules, true).Validate(invalidObject);
    }

    [TestMethod]
    public void TestNull()
    {
        Assert.IsTrue(failures.Contains(1));
    }

    [TestMethod]
    public void TestIn()
    {
        Assert.IsFalse(failures.Contains(2));
    }

    [TestMethod]
    public void TestBlank()
    {
        Assert.IsFalse(failures.Contains(3));
    }

    [TestMethod]
    public void TestRegex()
    {
        Assert.IsFalse(failures.Contains(4));
    }

    [TestMethod]
    public void TestBytes()
    {
        Assert.IsTrue(failures.Contains(5));
    }

    [TestMethod]
    public void TestStringLength()
    {
        Assert.IsFalse(failures.Contains(6));
    }

    [TestMethod]
    public void TestArrayLength()
    {
        Assert.IsFalse(failures.Contains(7));
    }

    [TestMethod]
    public void TestStringContains()
    {
        Assert.IsFalse(failures.Contains(8));
    }

    [TestMethod]
    public void TestArrayContains()
    {
        Assert.IsFalse(failures.Contains(9));
    }

    [TestMethod]
    public void TestRange()
    {
        Assert.IsFalse(failures.Contains(10));
    }

    [TestMethod]
    public void TestTrue()
    {
        Assert.IsFalse(failures.Contains(11));
    }

    [TestMethod]
    public void TestBlankField()
    {
        Assert.IsFalse(failures.Contains(12));
    }

    [TestMethod]
    public void TestBlankFieldFails()
    {
        Assert.IsTrue(failures.Contains(13));
    }

    [TestMethod]
    public void TestNullField()
    {
        Assert.IsFalse(failures.Contains(14));
    }

    [TestMethod]
    public void TestNullFieldFails()
    {
        Assert.IsTrue(failures.Contains(15));
    }

    [TestMethod]
    public void TestInFails()
    {
        Assert.IsTrue(failures.Contains(16));
    }

    [TestMethod]
    public void TestRegexFails()
    {
        Assert.IsTrue(failures.Contains(17));
    }

    [TestMethod]
    public void TestBytesRange()
    {
        Assert.IsFalse(failures.Contains(18));
    }

    [TestMethod]
    public void TestStringLengthFails()
    {
        Assert.IsTrue(failures.Contains(19));
    }

    [TestMethod]
    public void TestMapLength()
    {
        Assert.IsFalse(failures.Contains(20));
    }

    [TestMethod]
    public void TestSetLength()
    {
        Assert.IsFalse(failures.Contains(21));
    }

    [TestMethod]
    public void TestSetContains()
    {
        Assert.IsFalse(failures.Contains(22));
    }

    [TestMethod]
    public void TestArrayContainsFails()
    {
        Assert.IsTrue(failures.Contains(23));
    }

    [TestMethod]
    public void TestRangeInterval()
    {
        Assert.IsFalse(failures.Contains(24));
    }

    [TestMethod]
    public void TestRangeIntervalFails()
    {
        Assert.IsTrue(failures.Contains(25));
    }

    // arg / args 為 null 的情況(Java/TypeScript 已可正確解析,C# 需 TypeConverter 遷就)
    [TestMethod]
    public void TestInWithNullArg()
    {
        Assert.IsFalse(failures.Contains(29));
    }

    [TestMethod]
    public void TestArrayContainsNullArg()
    {
        Assert.IsFalse(failures.Contains(30));
    }

    [TestMethod]
    public void TestQuotedNullStringArg()
    {
        Assert.IsFalse(failures.Contains(31));
    }

    [TestMethod]
    public void TestRegexNegation()
    {
        Assert.IsFalse(failures.Contains(28));
    }

    [TestMethod]
    public void TestAnd1()
    {
        Assert.IsFalse(failures.Contains(102));
    }

    [TestMethod]
    public void TestAnd2()
    {
        Assert.IsTrue(failures.Contains(103));
    }

    [TestMethod]
    public void TestOr1()
    {
        Assert.IsFalse(failures.Contains(104));
    }

    [TestMethod]
    public void TestOr2()
    {
        Assert.IsFalse(failures.Contains(105));
    }

    [TestMethod]
    public void TestAndPassing()
    {
        Assert.IsTrue(failures.Contains(106));
    }

    [TestMethod]
    public void TestNotAnd()
    {
        Assert.IsTrue(failures.Contains(107));
    }

    [TestMethod]
    public void TestNotOr()
    {
        Assert.IsFalse(failures.Contains(108));
    }

    [TestMethod]
    public void TestNestedField()
    {
        Assert.IsFalse(failures.Contains(201));
    }

    [TestMethod]
    public void TestNestedNumericField()
    {
        Assert.IsFalse(failures.Contains(202));
    }

    [TestMethod]
    public void TestIndexedDateTime()
    {
        Assert.IsFalse(failures.Contains(203));
    }

    [TestMethod]
    public void TestMapKey()
    {
        Assert.IsFalse(failures.Contains(204));
    }

    [TestMethod]
    public void TestConcatSuffix()
    {
        Assert.IsFalse(failures.Contains(205));
    }

    [TestMethod]
    public void TestStarSuffix()
    {
        Assert.IsFalse(failures.Contains(206));
    }

    [TestMethod]
    public void TestEscapeSuffix()
    {
        Assert.IsFalse(failures.Contains(207));
    }

    [TestMethod]
    public void TestKeySuffix()
    {
        Assert.IsFalse(failures.Contains(208));
    }

    [TestMethod]
    public void TestIndexSuffix()
    {
        Assert.IsFalse(failures.Contains(209));
    }

    [TestMethod]
    public void TestFieldSuffix()
    {
        Assert.IsFalse(failures.Contains(210));
    }

    [TestMethod]
    public void TestNestedMapKey()
    {
        Assert.IsFalse(failures.Contains(211));
    }

    [TestMethod]
    public void TestSetIteration()
    {
        Assert.IsFalse(failures.Contains(212));
    }

    [TestMethod]
    public void TestFailureWithIdAndMessage()
    {
        Assert.IsTrue(failures.Contains(301));
    }

    [TestMethod]
    public void TestFailureWithMessageOnly()
    {
        Assert.AreEqual(1, result.failures.Count(f => f.id == null));
    }

    [TestMethod]
    public void TestPassedFlag()
    {
        Assert.IsFalse(result.passed);
    }

    [TestMethod]
    public void TestFailureCount()
    {
        Assert.AreEqual(16, result.failures.Count);
    }

    [TestMethod]
    public void TestFailureIds()
    {
        List<int> ids = [.. result.failures.Where(f => f.id != null).Select(f => f.id!.Value).Order()];
        CollectionAssert.AreEqual(
            new List<int> { 1, 5, 13, 15, 16, 17, 19, 23, 25, 27, 101, 103, 106, 107, 301 },
            ids
        );
    }

    [TestMethod]
    public void TestFailureMessages()
    {
        ValidationFailure? f301 = result.failures.FirstOrDefault(f => f.id == 301);
        Assert.IsNotNull(f301);
        Assert.AreEqual("\"testString\" does not match the required pattern.", f301!.message);
        ValidationFailure? fMsgOnly = result.failures.FirstOrDefault(f => f.id == null);
        Assert.IsNotNull(fMsgOnly);
        Assert.AreEqual("\"testInt\" is not in the allowed range.", fMsgOnly!.message);
    }

    [TestMethod]
    public void TestFailedFields()
    {
        Assert.AreEqual(7, result.failedFields.Count);
        Assert.IsTrue(result.failedFields.Contains("testString"));
        Assert.IsTrue(result.failedFields.Contains("testArray.*"));
        Assert.IsTrue(result.failedFields.Contains("testBlankString"));
        Assert.IsTrue(result.failedFields.Contains("testArray.0"));
        Assert.IsTrue(result.failedFields.Contains("testInt"));
        Assert.IsTrue(result.failedFields.Contains("testArray"));
        Assert.IsTrue(result.failedFields.Contains("testBool"));
    }

    [TestMethod]
    public void TestFastFail()
    {
        Assert.IsFalse(ffResult.passed);
        Assert.AreEqual(1, ffResult.failures.Count);
        Assert.IsTrue(ffResult.failures.First().id == 1);
        Assert.AreEqual(1, ffResult.failedFields.Count);
        Assert.IsTrue(ffResult.failedFields.Contains("testArray.*"));
    }

    [TestMethod]
    public void TestValidationTime()
    {
        Stopwatch stopwatch = Stopwatch.StartNew();
        ValidationResult freshResult = validator.Validate(testObject);
        stopwatch.Stop();
        Console.WriteLine(stopwatch.ElapsedMilliseconds + " ms");
        Assert.AreEqual(ffResult.failures.Count, freshResult.failures.Count);
        Assert.AreEqual(ffResult.failedFields.Count, freshResult.failedFields.Count);
    }

    [TestMethod]
    public void TestValidObject()
    {
        Assert.IsTrue(validResult.passed);
        Assert.AreEqual(0, validResult.failures.Count);
        Assert.AreEqual(0, validResult.failedFields.Count);
    }

    [TestMethod]
    public void TestInvalidObject()
    {
        Assert.IsFalse(invalidResult.passed);
        Assert.AreEqual(3, invalidResult.failures.Count);
        Assert.IsTrue(invalidResult.failures.Any(f => f.id == 1001));
        Assert.IsTrue(invalidResult.failures.Any(f => f.id == 1002));
        Assert.IsTrue(invalidResult.failures.Any(f => f.id == 1003));
        Assert.AreEqual(3, invalidResult.failedFields.Count);
        Assert.IsTrue(invalidResult.failedFields.Contains("testString"));
        Assert.IsTrue(invalidResult.failedFields.Contains("testInt"));
        Assert.IsTrue(invalidResult.failedFields.Contains("testBool"));
    }

    [TestMethod]
    public void TestInvalidObjectFastFail()
    {
        Assert.IsFalse(invalidFFResult.passed);
        Assert.AreEqual(1, invalidFFResult.failures.Count);
        ValidationFailure failure = invalidFFResult.failures.First();
        Assert.IsTrue(failure.id == 1001);
        Assert.AreEqual("\"testString\" must not be blank.", failure.message);
        Assert.AreEqual(1, invalidFFResult.failedFields.Count);
        Assert.IsTrue(invalidFFResult.failedFields.Contains("testString"));
    }
}
