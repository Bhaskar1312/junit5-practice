package com.example.junit5practice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// https://nipafx.dev/junit-5-parameterized-tests/
public class ParametrizedTests {

    @ParameterizedTest
    @ValueSource(strings={"hello", "world"})
    void parameterizedTest(String word) {
        assertNotNull(word);
    }


}
