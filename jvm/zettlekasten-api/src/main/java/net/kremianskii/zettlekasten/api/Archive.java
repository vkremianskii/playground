package net.kremianskii.zettlekasten.api;

import java.util.List;

import static net.kremianskii.common.Checks.checkNonNull;

public record Archive(List<Note> notes,
                      List<Category> categories) {

    public Archive {
        checkNonNull(notes, "notes");
        checkNonNull(categories, "categories");
    }
}
