package net.kremianskii.zettlekasten.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryNameTest {

    @Test
    void throws_if_value_is_null_or_empty_in_ctor() {
        // expect
        assertThrows(IllegalArgumentException.class, () -> new CategoryName(null));
        assertThrows(IllegalArgumentException.class, () -> new CategoryName(""));
        assertDoesNotThrow(() -> new CategoryName(" "));
    }
}
