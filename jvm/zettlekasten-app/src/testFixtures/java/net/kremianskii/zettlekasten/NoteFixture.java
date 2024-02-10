package net.kremianskii.zettlekasten;

import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import net.kremianskii.zettlekasten.domain.Note;

import java.util.Set;

public final class NoteFixture {

    private NoteFixture() {
    }

    public static Note aNote(final NoteName name,
                             final String text,
                             final Set<Tag> tags) {
        final var note = new Note(name);
        note.setText(text);
        for (final var tag : tags) {
            note.tag(tag);
        }
        return note;
    }
}
