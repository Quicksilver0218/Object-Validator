using System.Globalization;

class TestObject
{
    public string? testString;
    public int testInt;
    public bool testBool;
    public TestObject2?[]? testArray;
}

class TestObject2
{
    public DateTime testDateTime;

    public override string ToString()
    {
        return testDateTime.ToString("s", CultureInfo.InvariantCulture);
    }
}