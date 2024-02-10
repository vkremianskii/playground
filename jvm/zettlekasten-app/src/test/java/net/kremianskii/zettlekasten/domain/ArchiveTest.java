package net.kremianskii.zettlekasten.domain;

import net.kremianskii.zettlekasten.api.NoteId;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.util.UUID.randomUUID;
import static net.kremianskii.zettlekasten.NoteFixture.aNote;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArchiveTest {

    @Test
    void adds_a_note() {
        // given
        var archive = new Archive();
        var note = new Note(new NoteId(randomUUID()), new NoteName("name"));

        // when
        archive.add(note);

        // then
        assertEquals(List.of(note), archive.notes);
    }

    @Test
    void finds_notes_by_tag() {
        // given
        var tag = new Tag("value");
        var note1 = aNote(new NoteName("name"), "text", Set.of(tag));
        var note2 = aNote(new NoteName("name"), "text", Set.of());
        var note3 = aNote(new NoteName("name"), "text", Set.of(tag));
        var archive = new Archive();
        archive.add(note1);
        archive.add(note2);
        archive.add(note3);

        // when
        var tagged = archive.notesByTag(tag);

        // then
        assertEquals(List.of(note1, note3), tagged);
    }
}
