package com.example.junit5practice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

// https://nipafx.dev/junit-5-parameterized-tests/
class ParametrizedTests {

    @ParameterizedTest(name="run #{index} with [{arguments}]") // index starts at 1
    @ValueSource(strings={"hello", "world"})
    void parameterizedTest(String word) {
        assertNotNull(word);
    }

    @DisplayName("Roman numeral")
    @ParameterizedTest(name ="\"{0}\" should be {1}")
    @CsvSource({"I, 1", "II, 2", "III, 3",  "'Hello, JUnit 5!', 15" })
    void withNiceName(String word, int number) {

    }

    @ParameterizedTest
    @ValueSource(strings={"hello", "world"})
    void withOtherParams(String word, TestInfo info, TestReporter reporter) {
        reporter.publishEntry(info.getDisplayName(), "Word: " + word);
    }


    // meta annotations
    @Retention(RetentionPolicy.RUNTIME)
    @ParameterizedTest(name = "Elaborate name listing all {arguments}")
    @ValueSource(strings={"hello", "world"})
    public @interface Params {}

    @Params
    void testMetaAnnotation(String word) {
        assertNotNull(word);
    }

    // Arguments are provided by sources and you can use as many as you want for a test method
    // but need at least one or you get the aforementioned PreconditionViolationException.
    // @ValueSource - pick only one from strings, ints, longs, doubles, chars, booleans, classes, enums

    @ParameterizedTest
    @EnumSource(TimeUnit.class)
    void withAllEnumValues(TimeUnit unit) {
        assertNotNull(unit);
    }

    @ParameterizedTest
    @EnumSource(
        value = TimeUnit.class,
        names = {"NANOSECONDS", "HOURS"}
    )
    void withSomeEnumValues(TimeUnit unit) {
        assertNotNull(unit);
    }
    // note that @EnumSource only creates arguments for one parameter and so it can only be used on single-parameter methods.
    //  if you need more detailed control over which enum values are provided, take a look at @EnumSource's mode attribute.


    @ParameterizedTest
    @MethodSource("createWordsWithLength")
    void withMethodSource(String word, int length) { }

    private static Stream<Arguments> createWordsWithLength() {
        return Stream.of(
            Arguments.of("hello", 5),
            Arguments.of("Junit 5", 7)
        );
    }
    // Arguments is a simple interface wrapping an array of objects and Arguments.of(Object... args) creates an instance of it from the specified varargs.

    // The method called by @MethodSource must return a kind of collection, which can be any Stream (including the primitive specializations),
    // Iterable, Iterator, or array. It must be static, can be private, and doesn't have to be in the same class: @MethodSource("org.codefx.Words#provide") works, too.


    @ParameterizedTest
    @CsvFileSource(resources = "/word-lengths.csv")
    void withCsvSource(String word, int length) { }
}
