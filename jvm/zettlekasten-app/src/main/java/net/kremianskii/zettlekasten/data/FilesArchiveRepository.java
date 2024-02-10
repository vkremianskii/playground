package net.kremianskii.zettlekasten.data;

import net.kremianskii.common.FileUtil;
import net.kremianskii.zettlekasten.api.NoteId;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import net.kremianskii.zettlekasten.domain.Archive;
import net.kremianskii.zettlekasten.domain.ArchiveRepository;
import net.kremianskii.zettlekasten.domain.Note;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.UUID;

import static java.lang.System.lineSeparator;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.Files.list;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.walkFileTree;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.joining;
import static net.kremianskii.common.Checks.checkNonNull;
import static net.kremianskii.common.FunctionUtil.unchecked;

public final class FilesArchiveRepository implements ArchiveRepository {
    private final Path rootDirectory;

    public FilesArchiveRepository(final Path rootDirectory) {
        this.rootDirectory = checkNonNull(rootDirectory, "rootDirectory");
    }

    @Override
    public void save(Archive archive) throws IOException {
        try (final var files = list(rootDirectory)) {
            files.forEach(unchecked(FileUtil::deleteRecursively));
        }
        for (final var note : archive.notes) {
            final var notePath = Paths.get(rootDirectory.toString(), filenameFromNoteName(note.name()));
            try (final var writer = newBufferedWriter(notePath)) {
                writer.append(note.text());
                final var hashtags = note.tags.stream()
                    .map(tag -> "#" + tag.value)
                    .collect(joining(" "));
                if (!hashtags.isEmpty()) {
                    writer.newLine();
                    writer.append(hashtags);
                }
            }
        }
    }

    @Override
    public Optional<Archive> find() throws IOException {
        final var archive = new Archive();
        walkFileTree(rootDirectory, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return dir.equals(rootDirectory) ? CONTINUE : SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                final var filename = file.getFileName().toString();
                if (!filename.endsWith(".md")) {
                    return CONTINUE;
                }
                final var noteId = new NoteId(randomUUID());
                final var noteName = noteNameFromFilename(filename);
                final var note = new Note(noteId, noteName);
                try (final var reader = newBufferedReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("#")) {
                            final var hashtags = line.split(" ");
                            for (final var hashtag : hashtags) {
                                final var tag = hashtag.substring(1);
                                note.tag(new Tag(tag));
                            }
                        } else {
                            final var textBuilder = new StringBuilder();
                            final var noteText = note.text();
                            if (noteText != null && !noteText.isEmpty()) {
                                textBuilder.append(noteText);
                                textBuilder.append(lineSeparator());
                            }
                            textBuilder.append(line);
                            note.setText(textBuilder.toString());
                        }
                    }
                }
                archive.add(note);
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
        return Optional.of(archive);
    }

    private String filenameFromNoteName(final NoteName name) {
        return name.value + ".md";
    }

    private NoteName noteNameFromFilename(final String filename) {
        return new NoteName(filename.substring(0, filename.length() - 3));
    }
}
