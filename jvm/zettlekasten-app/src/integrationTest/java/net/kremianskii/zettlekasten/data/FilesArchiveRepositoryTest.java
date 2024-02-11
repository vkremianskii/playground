package net.kremianskii.zettlekasten.data;

import net.kremianskii.zettlekasten.api.CategoryName;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.readAllLines;
import static java.nio.file.Files.walkFileTree;
import static java.time.Instant.now;
import static net.kremianskii.common.FileUtil.deleteRecursively;
import static net.kremianskii.zettlekasten.ArchiveFixture.anArchive;
import static net.kremianskii.zettlekasten.CategoryFixture.aCategory;
import static net.kremianskii.zettlekasten.NoteFixture.aNote;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilesArchiveRepositoryTest {
    Path rootDirectory;

    @AfterEach
    void tearDown() throws IOException {
        if (rootDirectory != null) {
            deleteRecursively(rootDirectory);
        }
    }

    @Test
    void returns_empty_archive_if_root_dir_not_exists() throws IOException {
        // given
        var repository = new FilesArchiveRepository(Path.of("non-existent"));

        // when
        var archive = repository.find();

        // then
        assertTrue(archive.isEmpty());
    }

    @Test
    void finds_archive_in_directory() throws IOException {
        // given
        rootDirectory = createTempDirectory("zettlekasten");
        var repository = new FilesArchiveRepository(rootDirectory);
        var categoryAPath = Paths.get(rootDirectory.toString(), "category_a");
        createDirectory(categoryAPath);
        var categoryBPath = Paths.get(categoryAPath.toString(), "category_b");
        createDirectory(categoryBPath);
        var noteAPath = Paths.get(rootDirectory.toString(), "note_a.md");
        try (var writer = newBufferedWriter(noteAPath)) {
            writer.write("text");
            writer.newLine();
            writer.write("#tag1 #tag2");
        }
        var noteBPath = Paths.get(categoryBPath.toString(), "note_b.md");
        createFile(noteBPath);
        var nonNotePath = Paths.get(rootDirectory.toString(), "non-note.txt");
        createFile(nonNotePath);

        // when
        var archive = repository.find();

        // then
        assertTrue(archive.isPresent());
        assertEquals(2, archive.get().notes.size());
        var noteB = archive.get().notes.get(0);
        assertEquals(new NoteName("note_b"), noteB.name());
        assertTrue(noteB.text().isEmpty());
        assertTrue(noteB.tags.isEmpty());
        var noteA = archive.get().notes.get(1);
        assertEquals(new NoteName("note_a"), noteA.name());
        assertEquals("text", noteA.text());
        assertEquals(Set.of(new Tag("tag1"), new Tag("tag2")), noteA.tags);
        assertEquals(2, archive.get().categories.size());
        var categoryA = archive.get().categories.get(0);
        assertEquals(new CategoryName("category_a"), categoryA.name);
        assertTrue(categoryA.parentId().isEmpty());
        var categoryB = archive.get().categories.get(1);
        assertEquals(new CategoryName("category_b"), categoryB.name);
        assertTrue(categoryB.parentId().filter(categoryA.id::equals).isPresent());
    }

    @Test
    void saves_archive_to_directory() throws IOException {
        // given
        var tempDir = System.getProperty("java.io.tmpdir");
        rootDirectory = Paths.get(tempDir, "zettlekasten" + now().toEpochMilli());
        var repository = new FilesArchiveRepository(rootDirectory);
        var categoryA = aCategory(new CategoryName("category_a"), null);
        var categoryB = aCategory(new CategoryName("category_b"), categoryA.id);
        var archive = anArchive(
            List.of(
                aNote(
                    new NoteName("note_a"),
                    "text",
                    Set.of(new Tag("tag1"), new Tag("tag2")),
                    null),
                aNote(
                    new NoteName("note_b"),
                    "",
                    Set.of(),
                    categoryB.id)),
            List.of(categoryA, categoryB));

        // when
        repository.save(archive);

        // then
        var files = collectFiles(rootDirectory).stream().sorted().toList();
        assertEquals(4, files.size());
        assertEquals(Paths.get(rootDirectory.toString(), "category_a"), files.get(0));
        assertEquals(Paths.get(rootDirectory.toString(), "category_a", "category_b"), files.get(1));
        assertEquals(Paths.get(rootDirectory.toString(), "category_a", "category_b", "note_b.md"), files.get(2));
        assertEquals(Paths.get(rootDirectory.toString(), "note_a.md"), files.get(3));
        var noteALines = readAllLines(files.get(3));
        assertEquals(List.of("text", "#tag1 #tag2"), noteALines);
        var noteBLines = readAllLines(files.get(2));
        assertEquals(List.of(), noteBLines);
    }

    List<Path> collectFiles(Path root) throws IOException {
        var files = new ArrayList<Path>();
        walkFileTree(root, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!dir.equals(root)) {
                    files.add(dir);
                }
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                files.add(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return CONTINUE;
            }
        });
        return files;
    }
}
