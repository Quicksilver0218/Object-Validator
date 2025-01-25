using System.Diagnostics;
using Quicksilver.ObjectValidator;

namespace ObjectValidatorTest;
[TestClass]
public class UnitTest1
{
    private readonly Validator validator;
    private readonly ValidationResult result, ffResult;
    private readonly HashSet<int?> failures;

    public UnitTest1()
    {
        validator = new(File.OpenText("rules.json"));
        TestObject testObject = new(
            "test測試",
            1,
            true,
            [
                null,
                new(new(2023, 1, 1)),
                new(new(2024, 1, 1))
            ]
        );
        Stopwatch stopwatch = Stopwatch.StartNew();
        result = validator.Validate(testObject);
        stopwatch.Stop();
        Console.WriteLine(stopwatch.ElapsedMilliseconds + " ms");
        failures = result.failures.Select(f => f.id).ToHashSet();
        validator.fastFail = true;
        ffResult = validator.Validate(testObject);
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
    public void TestAnd1()
    {
        Assert.IsTrue(failures.Contains(101));
    }

    [TestMethod]
    public void TestOr1()
    {
        Assert.IsFalse(failures.Contains(102));
    }

    [TestMethod]
    public void TestAnd2()
    {
        Assert.IsTrue(failures.Contains(103));
    }

    [TestMethod]
    public void TestOr2()
    {
        Assert.IsFalse(failures.Contains(104));
    }

    [TestMethod]
    public void TestFailedFields()
    {
        Assert.AreEqual(2, result.failedFields.Count);
        Assert.IsTrue(result.failedFields.Contains("testString"));
        Assert.IsTrue(result.failedFields.Contains("testArray.*"));
    }

    [TestMethod]
    public void TestFastFail()
    {
        Assert.AreEqual(1, ffResult.failures.Count);
    }
}