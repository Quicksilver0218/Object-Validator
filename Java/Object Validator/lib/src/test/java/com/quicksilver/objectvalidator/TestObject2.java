package com.quicksilver.objectvalidator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TestObject2
{
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") {{ setTimeZone(TimeZone.getTimeZone("UTC")); }};

    public Date testDateTime;

    @Override
    public String toString()
    {
        return sdf.format(testDateTime);
    }
}