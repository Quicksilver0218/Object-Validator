package com.quicksilver.objectvalidator;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.quicksilver.objectvalidator.config.Condition;
import com.quicksilver.objectvalidator.config.Rule;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class LibraryTest {
    private final Validator validator;
    private final TestObject testObject;
    private final ValidationResult result, ffResult;
    private final Set<Integer> failures;
    private final Validator yamlValidator;
    private final ValidationResult yamlResult;
    private final Set<Integer> yamlFailures;
    private final ValidationResult validResult, invalidResult, invalidFFResult;

    LibraryTest() throws JsonMappingException, JsonProcessingException, IOException, URISyntaxException, ReflectiveOperationException {
        validator = new Validator(getClass().getResource("/rules.json"));
        testObject = new TestObject(
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
            },
            "   ",
            new TestNested("nestedValue", 2),
            createTestMap(),
            new HashSet<>(List.of("apple", "banana"))
        );
        result = validator.validate(testObject);
        failures = StreamSupport.stream(result.failures.spliterator(), false).map(f -> f.id).collect(Collectors.toSet());
        validator.fastFail = true;
        ffResult = validator.validate(testObject);

        yamlValidator = new Validator(getClass().getResource("/rules.yml"));
        yamlResult = yamlValidator.validate(testObject);
        yamlFailures = StreamSupport.stream(yamlResult.failures.spliterator(), false).map(f -> f.id).collect(Collectors.toSet());

        Rule[] scenarioRules = {
            new Rule(new Condition("!blank", "testString", null, null, null), 1001, "\"testString\" must not be blank."),
            new Rule(new Condition("range", "testInt", "[0,100]", null, null), 1002, null),
            new Rule(new Condition("true", "testBool", null, null, null), 1003, null)
        };
        TestObject validObject = new TestObject("hello", 42, true, new TestObject2[0], "value",
            new TestNested("nestedValue", 2), new HashMap<>(), new HashSet<>());
        TestObject invalidObject = new TestObject("", 999, false, new TestObject2[0], "  ",
            new TestNested("nestedValue", 2), new HashMap<>(), new HashSet<>());
        validResult = new Validator(scenarioRules).validate(validObject);
        invalidResult = new Validator(scenarioRules).validate(invalidObject);
        invalidFFResult = new Validator(scenarioRules, true).validate(invalidObject);
    }

    private static Map<String, Object> createTestMap() {
        Map<String, Object> nested = new HashMap<>();
        nested.put("inner", "innerValue");
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("part1.part2", "concatValue");
        map.put("*", "starValue");
        map.put("/A", "escapeValue");
        map.put("nested", nested);
        return map;
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
    void testBlankField() {
        assertFalse(failures.contains(12));
    }

    @Test
    void testBlankFieldFails() {
        assertTrue(failures.contains(13));
    }

    @Test
    void testNullField() {
        assertFalse(failures.contains(14));
    }

    @Test
    void testNullFieldFails() {
        assertTrue(failures.contains(15));
    }

    @Test
    void testInFails() {
        assertTrue(failures.contains(16));
    }

    @Test
    void testRegexFails() {
        assertTrue(failures.contains(17));
    }

    @Test
    void testBytesRange() {
        assertFalse(failures.contains(18));
    }

    @Test
    void testStringLengthFails() {
        assertTrue(failures.contains(19));
    }

    @Test
    void testMapLength() {
        assertFalse(failures.contains(20));
    }

    @Test
    void testSetLength() {
        assertFalse(failures.contains(21));
    }

    @Test
    void testSetContains() {
        assertFalse(failures.contains(22));
    }

    @Test
    void testArrayContainsFails() {
        assertTrue(failures.contains(23));
    }

    @Test
    void testRangeInterval() {
        assertFalse(failures.contains(24));
    }

    @Test
    void testRangeIntervalFails() {
        assertTrue(failures.contains(25));
    }

    @Test
    void testDateRange() {
        assertFalse(failures.contains(26));
    }

    @Test
    void testTrueFails() {
        assertTrue(failures.contains(27));
    }

    @Test
    void testRegexNegation() {
        assertFalse(failures.contains(28));
    }

    // Cases where arg / args is null (required fields being null)
    @Test
    void testInWithNullArg() {
        assertFalse(failures.contains(29));
    }

    @Test
    void testArrayContainsNullArg() {
        assertFalse(failures.contains(30));
    }

    @Test
    void testQuotedNullStringArg() {
        assertFalse(failures.contains(31));
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
    void testAndPassing() {
        assertFalse(failures.contains(105));
    }

    @Test
    void testOrFails() {
        assertTrue(failures.contains(106));
    }

    @Test
    void testNotAnd() {
        assertTrue(failures.contains(107));
    }

    @Test
    void testNotOr() {
        assertFalse(failures.contains(108));
    }

    @Test
    void testNestedField() {
        assertFalse(failures.contains(201));
    }

    @Test
    void testNestedNumericField() {
        assertFalse(failures.contains(202));
    }

    @Test
    void testIndexedDateTime() {
        assertFalse(failures.contains(203));
    }

    @Test
    void testMapKey() {
        assertFalse(failures.contains(204));
    }

    @Test
    void testConcatSuffix() {
        assertFalse(failures.contains(205));
    }

    @Test
    void testStarSuffix() {
        assertFalse(failures.contains(206));
    }

    @Test
    void testEscapeSuffix() {
        assertFalse(failures.contains(207));
    }

    @Test
    void testKeySuffix() {
        assertFalse(failures.contains(208));
    }

    @Test
    void testIndexSuffix() {
        assertFalse(failures.contains(209));
    }

    @Test
    void testFieldSuffix() {
        assertFalse(failures.contains(210));
    }

    @Test
    void testNestedMapKey() {
        assertFalse(failures.contains(211));
    }

    @Test
    void testSetIteration() {
        assertFalse(failures.contains(212));
    }

    @Test
    void testFailureWithIdAndMessage() {
        assertTrue(failures.contains(301));
    }

    @Test
    void testFailureWithMessageOnly() {
        assertEquals(1, result.failures.stream().filter(f -> f.id == null).count());
    }

    @Test
    void testPassedFlag() {
        assertFalse(result.passed);
    }

    @Test
    void testFailureCount() {
        assertEquals(16, result.failures.size());
    }

    @Test
    void testFailureIds() {
        List<Integer> ids = result.failures.stream().filter(f -> f.id != null).map(f -> f.id).sorted().toList();
        assertEquals(List.of(1, 5, 13, 15, 16, 17, 19, 23, 25, 27, 101, 103, 106, 107, 301), ids);
    }

    @Test
    void testFailureMessages() {
        ValidationFailure f301 = result.failures.stream().filter(f -> f.id != null && f.id == 301).findFirst().orElse(null);
        assertNotNull(f301);
        assertEquals("\"testString\" does not match the required pattern.", f301.message);
        ValidationFailure fMsgOnly = result.failures.stream().filter(f -> f.id == null).findFirst().orElse(null);
        assertNotNull(fMsgOnly);
        assertEquals("\"testInt\" is not in the allowed range.", fMsgOnly.message);
    }

    @Test
    void testFailedFields() {
        assertEquals(7, result.failedFields.size());
        assertTrue(result.failedFields.contains("testString"));
        assertTrue(result.failedFields.contains("testArray.*"));
        assertTrue(result.failedFields.contains("testBlankString"));
        assertTrue(result.failedFields.contains("testArray.0"));
        assertTrue(result.failedFields.contains("testInt"));
        assertTrue(result.failedFields.contains("testArray"));
        assertTrue(result.failedFields.contains("testBool"));
    }

    @Test
    void testFastFail() {
        assertFalse(ffResult.passed);
        assertEquals(1, ffResult.failures.size());
        ValidationFailure failure = ffResult.failures.iterator().next();
        assertEquals(Integer.valueOf(1), failure.id);
        assertEquals(1, ffResult.failedFields.size());
        assertTrue(ffResult.failedFields.contains("testArray.*"));
    }

    @Test
    void testValidationTime() throws ReflectiveOperationException {
        long start = System.currentTimeMillis();
        ValidationResult freshResult = validator.validate(testObject);
        System.out.println(System.currentTimeMillis() - start + " ms");
        assertEquals(ffResult.failures.size(), freshResult.failures.size());
        assertEquals(ffResult.failedFields.size(), freshResult.failedFields.size());
    }

    @Test
    void testValidObject() {
        assertTrue(validResult.passed);
        assertEquals(0, validResult.failures.size());
        assertEquals(0, validResult.failedFields.size());
    }

    @Test
    void testInvalidObject() {
        assertFalse(invalidResult.passed);
        assertEquals(3, invalidResult.failures.size());
        assertTrue(invalidResult.failures.stream().anyMatch(f -> f.id != null && f.id == 1001));
        assertTrue(invalidResult.failures.stream().anyMatch(f -> f.id != null && f.id == 1002));
        assertTrue(invalidResult.failures.stream().anyMatch(f -> f.id != null && f.id == 1003));
        assertEquals(3, invalidResult.failedFields.size());
        assertTrue(invalidResult.failedFields.contains("testString"));
        assertTrue(invalidResult.failedFields.contains("testInt"));
        assertTrue(invalidResult.failedFields.contains("testBool"));
    }

    @Test
    void testInvalidObjectFastFail() {
        assertFalse(invalidFFResult.passed);
        assertEquals(1, invalidFFResult.failures.size());
        ValidationFailure failure = invalidFFResult.failures.iterator().next();
        assertEquals(Integer.valueOf(1001), failure.id);
        assertEquals("\"testString\" must not be blank.", failure.message);
        assertEquals(1, invalidFFResult.failedFields.size());
        assertTrue(invalidFFResult.failedFields.contains("testString"));
    }

    // Tests for YAML rules.yml (corresponds to rules.json)
    @Test
    void testYamlFailureIds() {
        List<Integer> ids = yamlResult.failures.stream().filter(f -> f.id != null).map(f -> f.id).sorted().toList();
        assertEquals(List.of(1, 5, 13, 15, 16, 17, 19, 23, 25, 27, 101, 103, 106, 107, 301), ids);
    }

    @Test
    void testYamlInWithNullArg() {
        assertFalse(yamlFailures.contains(29));
    }

    @Test
    void testYamlArrayContainsNullArg() {
        assertFalse(yamlFailures.contains(30));
    }

    @Test
    void testYamlQuotedNullStringArg() {
        assertFalse(yamlFailures.contains(31));
    }

    // An exception should be thrown when a required field is null (consistent behavior across all three languages)
    private static RuntimeException getValidatorException(Rule[] rules) {
        try {
            new Validator(rules);
            return null;
        } catch (RuntimeException e) {
            return e;
        }
    }

    @Test
    void testNullCondition() {
        RuntimeException ex = getValidatorException(new Rule[] { new Rule(null, null, null) });
        assertNotNull(ex);
        assertEquals("Required rule field 'condition' is null.", ex.getMessage());
    }

    @Test
    void testNullType() {
        RuntimeException ex = getValidatorException(new Rule[] { new Rule(new Condition(null, "testString", null, null, null), null, null) });
        assertNotNull(ex);
        assertEquals("Required condition field 'type' is null.", ex.getMessage());
    }

    @Test
    void testInNullArgs() {
        RuntimeException ex = getValidatorException(new Rule[] { new Rule(new Condition("in", "testInt", null, null, null), null, null) });
        assertNotNull(ex);
        assertEquals("Required condition field 'args' is null for type 'in'.", ex.getMessage());
    }

    @Test
    void testAndNullConditions() {
        RuntimeException ex = getValidatorException(new Rule[] { new Rule(new Condition("and", null, null, null, null), null, null) });
        assertNotNull(ex);
        assertEquals("Required condition field 'conditions' is null for type 'and'.", ex.getMessage());
    }

    @Test
    void testOrNullConditions() {
        RuntimeException ex = getValidatorException(new Rule[] { new Rule(new Condition("or", null, null, null, null), null, null) });
        assertNotNull(ex);
        assertEquals("Required condition field 'conditions' is null for type 'or'.", ex.getMessage());
    }

    @Test
    void testRegexNullArg() {
        RuntimeException ex = getValidatorException(new Rule[] { new Rule(new Condition("regex", "testString", null, null, null), null, null) });
        assertNotNull(ex);
        assertEquals("Required condition field 'arg' is null for type 'regex'.", ex.getMessage());
    }

    @Test
    void testBytesNullArg() {
        RuntimeException ex = getValidatorException(new Rule[] { new Rule(new Condition("bytes", "testString", null, null, null), null, null) });
        assertNotNull(ex);
        assertEquals("Required condition field 'arg' is null for type 'bytes'.", ex.getMessage());
    }

    @Test
    void testLengthNullArg() {
        RuntimeException ex = getValidatorException(new Rule[] { new Rule(new Condition("length", "testString", null, null, null), null, null) });
        assertNotNull(ex);
        assertEquals("Required condition field 'arg' is null for type 'length'.", ex.getMessage());
    }

    @Test
    void testRangeNullArg() {
        RuntimeException ex = getValidatorException(new Rule[] { new Rule(new Condition("range", "testInt", null, null, null), null, null) });
        assertNotNull(ex);
        assertEquals("Required condition field 'arg' is null for type 'range'.", ex.getMessage());
    }

    @Test
    void testContainsNullArgString() {
        Validator v = new Validator(new Rule[] { new Rule(new Condition("contains", "testString", null, null, null), null, null) });
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            try {
                v.validate(testObject);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("Null argument is not supported for 'contains' with string values.", ex.getMessage());
    }

    @Test
    void testContainsNullArgIterable() throws ReflectiveOperationException {
        Validator v = new Validator(new Rule[] { new Rule(new Condition("contains", "testArray", null, null, null), null, null) });
        assertTrue(v.validate(testObject).passed);
    }
}
