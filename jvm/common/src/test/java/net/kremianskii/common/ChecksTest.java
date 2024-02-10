package net.kremianskii.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static net.kremianskii.common.Checks.checkEqual;
import static net.kremianskii.common.Checks.checkNonEmpty;
import static net.kremianskii.common.Checks.checkNonNull;
import static net.kremianskii.common.Checks.checkThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChecksTest {

    @Test
    void checks_that_value_is_non_null() {
        assertThrows(IllegalArgumentException.class, () -> checkNonNull(null, "value"));

        final var value = new Object();
        assertEquals(value, checkNonNull(value, "value"));
    }

    @Test
    void checks_that_value_is_non_empty() {
        assertThrows(IllegalArgumentException.class, () -> checkNonEmpty(null, "value"));
        assertThrows(IllegalArgumentException.class, () -> checkNonEmpty("", "value"));

        final var value = " ";
        assertEquals(value, checkNonNull(value, "value"));
    }

    @Test
    void checks_that_values_are_equal() {
        assertThrows(IllegalArgumentException.class, () -> checkEqual(0, 1, "values must be equal"));
        assertDoesNotThrow(() -> checkEqual("", "", "values must be equal"));
    }

    @Test
    void checks_that_condition_is_met() {
        assertThrows(IllegalArgumentException.class, () -> checkThat(
            "",
            value -> !value.isEmpty(),
            "value must not be empty"));

        final var value = "";
        assertEquals(value, checkThat(
            value,
            Objects::nonNull,
            "value must not be null"));
    }
}
