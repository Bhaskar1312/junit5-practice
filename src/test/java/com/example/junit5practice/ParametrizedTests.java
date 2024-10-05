package com.example.junit5practice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    @CsvSource({"I, 1", "II, 2", "III, 3"})
    void withNiceName(String word, int number) {

    }


}
