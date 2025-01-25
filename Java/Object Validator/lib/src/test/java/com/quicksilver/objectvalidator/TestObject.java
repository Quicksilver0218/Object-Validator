package com.quicksilver.objectvalidator;

public class TestObject {
    private final String testString;
    private final int testInt;
    private final boolean testBool;
    private final TestObject2[] testArray;

    public TestObject(String testString, int testInt, boolean testBool, TestObject2[] testArray) {
        this.testString = testString;
        this.testInt = testInt;
        this.testBool = testBool;
        this.testArray = testArray;
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
}