package net.kremianskii.zettlekasten.api;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

import static net.kremianskii.common.Checks.checkNonNull;

public record Note(NoteName name,
                   String text,
                   Set<Tag> tags,
                   @Nullable Category category) {

    public Note {
        checkNonNull(name, "name");
        checkNonNull(text, "text");
        checkNonNull(tags, "tags");
    }
}
