package net.kremianskii.zettlekasten.data;

import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.list;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.readAllLines;
import static java.nio.file.Files.walkFileTree;
import static net.kremianskii.zettlekasten.ArchiveFixture.anArchive;
import static net.kremianskii.zettlekasten.NoteFixture.aNote;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilesArchiveRepositoryTest {
    Path rootDirectory;

    @BeforeEach
    void setup() throws IOException {
        rootDirectory = createTempDirectory("zettlekasten");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (rootDirectory != null) {
            deleteRecursively(rootDirectory);
        }
    }

    @Test
    void finds_archive_in_directory() throws IOException {
        // given
        var repository = new FilesArchiveRepository(rootDirectory);
        var notePath = Paths.get(rootDirectory.toString(), "note.md");
        try (var noteWriter = newBufferedWriter(notePath)) {
            noteWriter.write("text");
            noteWriter.newLine();
            noteWriter.write("#tag1 #tag2");
        }
        var nonNotePath = Paths.get(rootDirectory.toString(), "non-note.txt");
        createFile(nonNotePath);
        var subDirectory = Paths.get(rootDirectory.toString(), "sub");
        createDirectory(subDirectory);
        var subNotePath = Paths.get(subDirectory.toString(), "sub-note.md");
        createFile(subNotePath);

        // when
        var archive = repository.find();

        // then
        assertTrue(archive.isPresent());
        assertEquals(
            anArchive(List.of(aNote(
                new NoteName("note"),
                "text",
                Set.of(new Tag("tag1"), new Tag("tag2")))
            )),
            archive.get());
    }

    @Test
    void saves_archive_to_directory() throws IOException {
        // given
        var repository = new FilesArchiveRepository(rootDirectory);
        var archive = anArchive(List.of(
            aNote(
                new NoteName("name1"),
                "text",
                Set.of(new Tag("tag1"), new Tag("tag2"))),
            aNote(new NoteName("name2"), "", Set.of())
        ));

        // when
        repository.save(archive);

        // then
        try (var files = list(rootDirectory)) {
            var filesList = files.sorted().toList();
            assertEquals(2, filesList.size());
            assertEquals("name1.md", filesList.get(0).getFileName().toString());
            var file1Lines = readAllLines(filesList.get(0));
            assertEquals(List.of("text", "#tag1 #tag2"), file1Lines);
            assertEquals("name2.md", filesList.get(1).getFileName().toString());
            var file2Lines = readAllLines(filesList.get(1));
            assertEquals(List.of(), file2Lines);
        }
    }

    void deleteRecursively(Path path) throws IOException {
        walkFileTree(path, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                delete(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                delete(dir);
                return CONTINUE;
            }
        });
    }
}
