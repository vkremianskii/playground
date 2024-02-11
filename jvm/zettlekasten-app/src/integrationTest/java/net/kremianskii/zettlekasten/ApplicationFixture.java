package net.kremianskii.zettlekasten;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import net.kremianskii.zettlekasten.config.Config;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.URI;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.notExists;
import static net.kremianskii.common.FileUtil.deleteRecursively;
import static net.kremianskii.zettlekasten.Application.parseConfig;

public class ApplicationFixture {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new Jdk8Module());
    protected static Config config;

    static Application application;

    @BeforeAll
    static void setup() throws IOException {
        config = parseConfig(URI.create("classpath:/test-config.yml"));
        if (notExists(config.zettlekasten().dir())) {
            createDirectories(config.zettlekasten().dir());
        }
        application = new Application(config);
        application.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        application.stop();
        deleteRecursively(config.zettlekasten().dir());
    }
}
