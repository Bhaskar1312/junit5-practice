package com.example.junit5practice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;

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

    // @ParameterizedTest
    // @CsvSource({ "(0/0), 0", "(0/1), 1", "(1/1), 1.414" })
    // void convertPointNorm(@ConvertPoint Point point, double norm) { }

    @ParameterizedTest
    @CsvSource({"true, 3.14159265359, AUGUST, 2018, 2018-08-23T22:00:00"})
    void testDefaultConverters(
        boolean b, double d, Summer s, Year y, LocalDateTime dt) { }

    enum Summer {
        JUNE, JULY, AUGUST, SEPTEMBER;
    }

    @ParameterizedTest
    @CsvSource({ "0, 0, 0", "1, 0, 1", "1.414, 1, 1" })
    void testPointNorm(double norm, ArgumentsAccessor arguments) {
        Point point = Point.from(
            arguments.getDouble(1), arguments.getDouble(2));
        /*...*/
    }
    record Point(double x, double y) {
        static Point from(double x, double y) {
            return new Point(x, y);
        }
    }

    @ParameterizedTest
    @CsvSource({ "0, 0, 0", "1, 0, 1", "1.414, 1, 1" })
    void testPointNorm(
        double norm,
        @AggregateWith(PointAggregator.class) Point point) {
        /*...*/
    }

    static class PointAggregator implements ArgumentsAggregator {

        @Override
        public Object aggregateArguments(
            ArgumentsAccessor arguments, ParameterContext context)
            throws ArgumentsAggregationException {
            return Point.from(
                arguments.getDouble(1), arguments.getDouble(2));
        }

    }
    // Argument Accessors And Aggregators


    // https://mikemybytes.com/2021/10/19/parameterize-like-a-pro-with-junit-5-csvsource/
    @ParameterizedTest
    @CsvSource(textBlock = """
        hello world, 11
        Junit, 5, 7
        Junit 5, 7
        """)
    void calculatesPhraseLength(String phrase, int expectedLength) {
        Assertions.assertEquals(expectedLength, phrase.length());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
    Hello world!    | Hallo Welt!   | 12
    Spock           | JUnit Jupiter | 13
                    | Java          |  4
    ''              | ''            |  0
""")
    void calculatesMaxLength(String phrase1, String phrase2, int expected) {
        int actual = Integer.max(phrase1==null? 0:phrase1.length(), phrase2.length());
        Assertions.assertEquals(expected, actual);
    }
    // An empty, quoted value '' results in an empty String unless the emptyValue attribute is set; whereas, an entirely empty value is interpreted as a null reference.

    @ParameterizedTest
    @CsvSource(delimiterString = "->", textBlock = """
    fooBar        -> FooBar
    junit_jupiter -> JunitJupiter
    CsvSource     -> CsvSource
""")
    void convertsToUpperCamelCase(String input, String expected) {
        // String converted = caseConverter.toUpperCamelCase(input);
        // Assertions.assertEquals(expected, converted);
    }

    @ParameterizedTest
    @CsvSource(delimiterString = "maps to", textBlock = """
    'foo'    maps to  'bar'
    'junit'  maps to  'jupiter'
""")
    void shouldMapPair(String input, String expected) {
        // String actual = pairMapper.map(input);
        // Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{index} => calculates the sum of {0}: ({1}, {2})")
    @CsvSource(delimiter = '|', textBlock = """
    positive numbers      |   10  |      6  |   16
    positive and negative |   -4  |      2  |   -2
    negative numbers      |   -6  |   -100  | -106
""")
    void calculatesSum(String description, int a, int b, int expectedSum) {
        int actual = a + b;
        Assertions.assertEquals(expectedSum, actual);
    }



}
