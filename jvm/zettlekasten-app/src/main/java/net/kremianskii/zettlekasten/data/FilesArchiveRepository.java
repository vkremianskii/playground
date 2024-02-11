package net.kremianskii.zettlekasten.data;

import net.kremianskii.common.FileUtil;
import net.kremianskii.zettlekasten.api.CategoryId;
import net.kremianskii.zettlekasten.api.CategoryName;
import net.kremianskii.zettlekasten.api.NoteId;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import net.kremianskii.zettlekasten.domain.Archive;
import net.kremianskii.zettlekasten.domain.ArchiveRepository;
import net.kremianskii.zettlekasten.domain.Category;
import net.kremianskii.zettlekasten.domain.Note;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import static java.lang.System.lineSeparator;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.list;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.notExists;
import static java.nio.file.Files.walkFileTree;
import static java.util.Comparator.comparing;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static net.kremianskii.common.Checks.checkNonNull;
import static net.kremianskii.common.FunctionUtil.unchecked;

public final class FilesArchiveRepository implements ArchiveRepository {
    private final Path rootDirectory;

    public FilesArchiveRepository(final Path rootDirectory) {
        this.rootDirectory = checkNonNull(rootDirectory, "rootDirectory");
    }

    @Override
    public void save(Archive archive) throws IOException {
        if (notExists(rootDirectory)) {
            createDirectory(rootDirectory);
        }
        try (final var files = list(rootDirectory)) {
            files.forEach(unchecked(FileUtil::deleteRecursively));
        }
        final var idToCategory = archive.categories.stream()
            .collect(toUnmodifiableMap(cat -> cat.id, identity()));
        final var categoryIdToPath = new HashMap<CategoryId, Path>();
        for (final var category : archive.categories) {
            final var components = pathComponents(category, idToCategory);
            categoryIdToPath.put(category.id, pathFromComponents(components));
        }
        categoryIdToPath.values().stream()
            .sorted(comparing(Path::toString))
            .forEach(path -> {
                final var absPath = Paths.get(rootDirectory.toString(), path.toString());
                try {
                    createDirectories(absPath);
                } catch (final IOException exc) {
                    throw new RuntimeException(exc);
                }
            });
        for (final var note : archive.notes) {
            final var components = new LinkedList<String>();
            components.add(rootDirectory.toString());
            note.categoryId()
                .map(categoryIdToPath::get)
                .ifPresent(categoryPath -> components.add(categoryPath.toString()));
            components.add(filenameFromNoteName(note.name()));
            final var notePath = pathFromComponents(components);
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
        if (notExists(rootDirectory)) {
            return empty();
        }
        final var archive = new Archive();
        final var categories = new ArrayList<Category>();
        final var categoriesStack = new Stack<Category>();
        walkFileTree(rootDirectory, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!dir.equals(rootDirectory)) {
                    final var category = new Category(
                        new CategoryId(randomUUID()),
                        new CategoryName(dir.getFileName().toString()),
                        !categoriesStack.empty() ? categoriesStack.peek().id : null);
                    categories.add(category);
                    categoriesStack.push(category);
                }
                return CONTINUE;
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
                if (!categoriesStack.empty()) {
                    note.setCategoryId(categoriesStack.peek().id);
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
                if (!dir.equals(rootDirectory)) {
                    categoriesStack.pop();
                }
                return CONTINUE;
            }
        });
        categories.forEach(archive::add);
        return Optional.of(archive);
    }

    private List<String> pathComponents(final Category category,
                                        final Map<CategoryId, Category> idToCategory) {
        final var components = new LinkedList<String>();
        final var stack = new Stack<Category>();
        stack.push(category);
        while (!stack.empty()) {
            final var stackCategory = stack.pop();
            components.addFirst(stackCategory.name.value);
            stackCategory.parentId()
                .map(idToCategory::get)
                .ifPresent(stack::push);
        }
        return components;
    }

    private Path pathFromComponents(final List<String> components) {
        if (components.isEmpty()) {
            throw new IllegalArgumentException("components must not be empty");
        }
        final var first = components.get(0);
        if (components.size() == 1) {
            return Path.of(first);
        }
        final var more = components.subList(1, components.size());
        return Paths.get(first, more.toArray(new String[0]));
    }

    private String filenameFromNoteName(final NoteName name) {
        return name.value + ".md";
    }

    private NoteName noteNameFromFilename(final String filename) {
        return new NoteName(filename.substring(0, filename.length() - 3));
    }
}
