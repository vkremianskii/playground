package net.kremianskii.zettlekasten;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.walkFileTree;

public class ApplicationFixture {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new Jdk8Module());
    protected static Path zettlekastenDirectory;

    static Application application;
    static AtomicBoolean cancelled;
    static Thread thread;

    @BeforeAll
    static void setup() throws IOException {
        application = new Application();
        zettlekastenDirectory = createTempDirectory("zettlekasten");
        cancelled = new AtomicBoolean(false);
        thread = new Thread(() -> {
            try {
                application.run(zettlekastenDirectory, cancelled::get);
            } catch (final IOException ignored) {
            }
        });
        thread.start();
    }

    @AfterAll
    static void tearDown() throws InterruptedException, IOException {
        deleteRecursively(zettlekastenDirectory);
        cancelled.set(true);
        thread.join();
    }

    static void deleteRecursively(Path path) throws IOException {
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
