using System.Globalization;

class TestObject(
    string? testString,
    int testInt,
    bool testBool,
    TestObject2?[]? testArray,
    string testBlankString,
    TestNested testNested,
    Dictionary<string, object?> testMap,
    HashSet<string> testSet)
{
    private readonly string? testString = testString;
    private readonly int testInt = testInt;
    private readonly bool testBool = testBool;
    private readonly TestObject2?[]? testArray = testArray;
    private readonly string testBlankString = testBlankString;
    private readonly TestNested testNested = testNested;
    private readonly Dictionary<string, object?> testMap = testMap;
    private readonly HashSet<string> testSet = testSet;

    public string? TestString => testString;
    public int TestInt => testInt;
    public bool TestBool => testBool;
    public TestObject2?[]? TestArray => testArray;
    public string TestBlankString => testBlankString;
    public TestNested TestNested => testNested;
    public Dictionary<string, object?> TestMap => testMap;
    public HashSet<string> TestSet => testSet;
}

class TestNested(string name, int count)
{
    private readonly string name = name;
    private readonly int count = count;

    public string Name => name;
    public int Count => count;
}

class TestObject2(DateTime testDateTime)
{
    private readonly DateTime testDateTime = testDateTime;

    public override string ToString()
    {
        return testDateTime.ToString("s", CultureInfo.InvariantCulture);
    }
}
