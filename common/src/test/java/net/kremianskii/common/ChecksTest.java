package net.kremianskii.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static net.kremianskii.common.Checks.checkNonEmpty;
import static net.kremianskii.common.Checks.checkNonNull;
import static net.kremianskii.common.Checks.checkThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChecksTest {

    @Test
    void checks_that_value_is_non_null() {
        assertThrows(IllegalArgumentException.class, () -> checkNonNull(null, "value"));
        Assertions.assertDoesNotThrow(() -> checkNonNull(new Object(), "value"));
    }

    @Test
    void checks_that_value_is_non_empty() {
        assertThrows(IllegalArgumentException.class, () -> checkNonEmpty(null, "value"));
        assertThrows(IllegalArgumentException.class, () -> checkNonEmpty("", "value"));
        Assertions.assertDoesNotThrow(() -> checkNonEmpty(" ", "value"));
    }

    @Test
    void checks_that_condition_is_met() {
        assertThrows(IllegalArgumentException.class, () -> checkThat(
            "",
            value -> !value.isEmpty(),
            "value must not be empty"));
        Assertions.assertDoesNotThrow(() -> checkThat(
            "",
            Objects::nonNull,
            "value must not be null"));
    }
}
