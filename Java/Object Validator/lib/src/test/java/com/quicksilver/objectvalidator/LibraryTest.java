package com.quicksilver.objectvalidator;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class LibraryTest {
    private final Validator validator;
    private final ValidationResult result, ffResult;
    private final Set<Integer> failures;

    LibraryTest() throws JsonMappingException, JsonProcessingException, IOException, URISyntaxException, ReflectiveOperationException {
        validator = new Validator(getClass().getResource("/rules.json"));
        TestObject testObject = new TestObject(
            "test測試",
            1,
            true,
            new TestObject2[] {
                null,
                new TestObject2(
                    Date.from(ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, TimeZone.getTimeZone("UTC").toZoneId()).toInstant())
                ),
                new TestObject2(
                    Date.from(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, TimeZone.getTimeZone("UTC").toZoneId()).toInstant())
                )
            }
        );
        long start = System.currentTimeMillis();
        result = validator.validate(testObject);
        System.out.println(System.currentTimeMillis() - start + " ms");
        failures = StreamSupport.stream(result.failures.spliterator(), false).map(f -> f.id).collect(Collectors.toSet());
        validator.fastFail = true;
        ffResult = validator.validate(testObject);
    }

    @Test
    void testNull() {
        assertTrue(failures.contains(1));
    }

    @Test
    void testIn() {
        assertFalse(failures.contains(2));
    }

    @Test
    void testBlank() {
        assertFalse(failures.contains(3));
    }

    @Test
    void testRegex() {
        assertFalse(failures.contains(4));
    }

    @Test
    void testBytes() {
        assertTrue(failures.contains(5));
    }

    @Test
    void testStringLength() {
        assertFalse(failures.contains(6));
    }

    @Test
    void testArrayLength() {
        assertFalse(failures.contains(7));
    }

    @Test
    void testStringContains() {
        assertFalse(failures.contains(8));
    }

    @Test
    void testArrayContains() {
        assertFalse(failures.contains(9));
    }

    @Test
    void testRange() {
        assertFalse(failures.contains(10));
    }

    @Test
    void testTrue() {
        assertFalse(failures.contains(11));
    }

    @Test
    void testAnd1() {
        assertTrue(failures.contains(101));
    }

    @Test
    void testOr1() {
        assertFalse(failures.contains(102));
    }

    @Test
    void testAnd2() {
        assertTrue(failures.contains(103));
    }

    @Test
    void testOr2() {
        assertFalse(failures.contains(104));
    }

    @Test
    void testFailedFields() {
        assertEquals(2, result.failedFields.size());
        assertTrue(result.failedFields.contains("testString"));
        assertTrue(result.failedFields.contains("testArray.*"));
    }

    @Test
    void testFastFail() {
        assertEquals(1, ffResult.failures.size());
    }
}
