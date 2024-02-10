package net.kremianskii.zettlekasten.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TagTest {

    @Test
    void throws_if_value_does_not_match_pattern_in_ctor() {
        assertThrows(IllegalArgumentException.class, () -> new Tag(null));
        assertThrows(IllegalArgumentException.class, () -> new Tag(""));
        assertThrows(IllegalArgumentException.class, () -> new Tag(" "));
        assertThrows(IllegalArgumentException.class, () -> new Tag("a 0"));
        assertDoesNotThrow(() -> new Tag("a_0"));
    }
}
