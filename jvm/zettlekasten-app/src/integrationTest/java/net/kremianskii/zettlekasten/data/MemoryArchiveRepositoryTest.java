package net.kremianskii.zettlekasten.data;

import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.kremianskii.zettlekasten.ArchiveFixture.anArchive;
import static net.kremianskii.zettlekasten.NoteFixture.aNote;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryArchiveRepositoryTest {

    @Test
    void saves_archive_in_memory() {
        // given
        var repository = new MemoryArchiveRepository();
        var archive = anArchive(List.of(aNote(
            new NoteName("name"),
            "text",
            Set.of(new Tag("tag")))));

        // expect
        assertDoesNotThrow(() -> repository.save(archive));
    }

    @Test
    void finds_archive_in_memory() {
        // given
        var repository = new MemoryArchiveRepository();
        var archive = anArchive(List.of(aNote(
            new NoteName("name"),
            "text",
            Set.of(new Tag("tag")))));
        repository.save(archive);

        // when
        var found = repository.find();

        // then
        assertEquals(Optional.of(archive), found);
    }
}
