package net.kremianskii.zettlekasten.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryNameTest {

    @Test
    void throws_if_name_does_not_match_pattern_in_ctor() {
        assertThrows(IllegalArgumentException.class, () -> new CategoryName(null));
        assertThrows(IllegalArgumentException.class, () -> new CategoryName(""));
        assertDoesNotThrow(() -> new CategoryName("aZ0_ "));
    }
}
