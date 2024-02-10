package net.kremianskii.zettlekasten;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.walkFileTree;

public class ApplicationFixture {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new Jdk8Module());
    protected static Path zettlekastenDirectory;
    protected static InetSocketAddress address;

    static Application application;

    @BeforeAll
    static void setup() throws IOException {
        zettlekastenDirectory = createTempDirectory("zettlekasten");
        address = new InetSocketAddress("localhost", 8080);
        application = new Application(zettlekastenDirectory, address);
        application.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        application.stop();
        deleteRecursively(zettlekastenDirectory);
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
