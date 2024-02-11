package net.kremianskii.zettlekasten;

import net.kremianskii.zettlekasten.domain.Archive;
import net.kremianskii.zettlekasten.domain.Category;
import net.kremianskii.zettlekasten.domain.Note;

import java.util.List;

public final class ArchiveFixture {

    private ArchiveFixture() {
    }

    public static Archive anArchive(final List<Note> notes,
                                    final List<Category> categories) {
        final var archive = new Archive();
        notes.forEach(archive::add);
        categories.forEach(archive::add);
        return archive;
    }
}
