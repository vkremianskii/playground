package net.kremianskii.zettlekasten;

import net.kremianskii.zettlekasten.api.CategoryId;
import net.kremianskii.zettlekasten.api.NoteId;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import net.kremianskii.zettlekasten.domain.Note;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static java.util.UUID.randomUUID;

public final class NoteFixture {

    private NoteFixture() {
    }

    public static Note aNote(final NoteName name,
                             final String text,
                             final Set<Tag> tags,
                             @Nullable CategoryId categoryId) {
        final var note = new Note(new NoteId(randomUUID()), name);
        note.setText(text);
        for (final var tag : tags) {
            note.tag(tag);
        }
        note.setCategoryId(categoryId);
        return note;
    }
}
