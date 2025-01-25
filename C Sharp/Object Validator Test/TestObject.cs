using System.Globalization;

class TestObject(string? testString, int testInt, bool testBool, TestObject2?[]? testArray)
{
    private readonly string? testString = testString;
    private readonly int testInt = testInt;
    private readonly bool testBool = testBool;
    private readonly TestObject2?[]? testArray = testArray;

    public string? TestString => testString;
    public int TestInt => testInt;
    public bool TestBool => testBool;
    public TestObject2?[]? TestArray => testArray;
}

class TestObject2(DateTime testDateTime)
{
    private readonly DateTime testDateTime = testDateTime;

    public override string ToString()
    {
        return testDateTime.ToString("s", CultureInfo.InvariantCulture);
    }
}