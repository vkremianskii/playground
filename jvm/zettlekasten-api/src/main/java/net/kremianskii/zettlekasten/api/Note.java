package net.kremianskii.zettlekasten.api;

import java.util.Set;

import static net.kremianskii.common.Checks.checkNonNull;

public record Note(NoteName name,
                   String text,
                   Set<Tag> tags) {

    public Note {
        checkNonNull(name, "name");
        checkNonNull(text, "text");
        checkNonNull(tags, "tags");
    }
}
