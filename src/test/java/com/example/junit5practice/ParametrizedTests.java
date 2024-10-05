package com.example.junit5practice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.params.ParameterizedTest;

// https://nipafx.dev/junit-5-parameterized-tests/
public class ParametrizedTests {
    @ParameterizedTest
    void parameterizedTest(String word) {
        assertNotNull(word);
    }


}
