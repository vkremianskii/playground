package net.kremianskii.zettlekasten.api;

import java.util.List;

import static net.kremianskii.common.Checks.checkNonNull;

public record Archive(List<Note> notes) {

    public Archive {
        checkNonNull(notes, "notes");
    }
}
