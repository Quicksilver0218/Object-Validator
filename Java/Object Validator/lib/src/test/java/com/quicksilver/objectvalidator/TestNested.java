package com.quicksilver.objectvalidator;

public class TestNested {
    private final String name;
    private final int count;

    public TestNested(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
