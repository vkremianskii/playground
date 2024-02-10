package net.kremianskii.zettlekasten.api;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static net.kremianskii.common.Checks.checkNonNull;

public record Note(NoteId id,
                   NoteName name,
                   String text,
                   Set<Tag> tags,
                   @Nullable CategoryId categoryId) {

    public Note {
        checkNonNull(id, "id");
        checkNonNull(name, "name");
        checkNonNull(text, "text");
        checkNonNull(tags, "tags");
    }
}
