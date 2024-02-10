package net.kremianskii.zettlekasten.domain;

import net.kremianskii.zettlekasten.api.NoteId;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteTest {

    @Test
    void renames() {
        // given
        var note = new Note(new NoteId(randomUUID()), new NoteName("old name"));

        // when
        note.rename(new NoteName("new name"));

        // then
        assertEquals(new NoteName("new name"), note.name());
    }

    @Test
    void sets_text() {
        // given
        var note = new Note(new NoteId(randomUUID()), new NoteName("name"));

        // when
        note.setText("text");

        // then
        assertEquals("text", note.text());
    }

    @Test
    void throws_if_text_is_null_on_set_text() {
        // given
        var note = new Note(new NoteId(randomUUID()), new NoteName("name"));

        // expect
        assertThrows(IllegalArgumentException.class, () -> note.setText(null));
    }

    @Test
    void adds_a_tag() {
        // given
        var note = new Note(new NoteId(randomUUID()), new NoteName("name"));
        var tag = new Tag("value");

        // when
        note.tag(tag);

        // then
        assertTrue(note.tagged(tag));
    }
}
