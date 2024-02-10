package net.kremianskii.zettlekasten;

import net.kremianskii.zettlekasten.domain.Archive;
import net.kremianskii.zettlekasten.domain.Note;

import java.util.List;

public final class ArchiveFixture {

    private ArchiveFixture() {
    }

    public static Archive anArchive(final List<Note> notes) {
        final var archive = new Archive();
        for (final var note : notes) {
            archive.add(note);
        }
        return archive;
    }
}
