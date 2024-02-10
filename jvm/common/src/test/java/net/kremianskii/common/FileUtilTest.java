package net.kremianskii.common;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.notExists;
import static net.kremianskii.common.FileUtil.deleteRecursively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilTest {

    @Test
    void delete_directory_recursively() throws IOException {
        // given
        var rootDir = createTempDirectory("root");
        var childDir = createDirectory(Paths.get(rootDir.toString(), "child"));
        var grandchildDir = createDirectory(Paths.get(childDir.toString(), "grandchild"));

        // when
        deleteRecursively(rootDir);

        // then
        assertTrue(notExists(rootDir));
    }
}
