package net.kremianskii.zettlekasten.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {

    @Test
    void is_parsed_from_yaml() throws IOException {
        // given
        var mapper = new ObjectMapper(new YAMLFactory());
        var yaml = """
            server:
                port: 8080
            zettlekasten:
                dir: ~/zettlekasten
            """;

        // when
        var config = mapper.readValue(yaml, Config.class);

        // then
        assertEquals(8080, config.server().port());
        assertEquals(Paths.get("~", "zettlekasten"), config.zettlekasten().dir());
    }
}
