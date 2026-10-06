package com.quicksilver.objectvalidator;

import java.util.Map;
import java.util.Set;

public class TestObject {
    private final String testString;
    private final int testInt;
    private final boolean testBool;
    private final TestObject2[] testArray;
    private final String testBlankString;
    private final TestNested testNested;
    private final Map<String, Object> testMap;
    private final Set<String> testSet;

    public TestObject(String testString, int testInt, boolean testBool, TestObject2[] testArray,
            String testBlankString, TestNested testNested, Map<String, Object> testMap, Set<String> testSet) {
        this.testString = testString;
        this.testInt = testInt;
        this.testBool = testBool;
        this.testArray = testArray;
        this.testBlankString = testBlankString;
        this.testNested = testNested;
        this.testMap = testMap;
        this.testSet = testSet;
    }

    public String getTestString() {
        return testString;
    }

    public int getTestInt() {
        return testInt;
    }

    public boolean isTestBool() {
        return testBool;
    }

    public TestObject2[] getTestArray() {
        return testArray;
    }

    public String getTestBlankString() {
        return testBlankString;
    }

    public TestNested getTestNested() {
        return testNested;
    }

    public Map<String, Object> getTestMap() {
        return testMap;
    }

    public Set<String> getTestSet() {
        return testSet;
    }
}
