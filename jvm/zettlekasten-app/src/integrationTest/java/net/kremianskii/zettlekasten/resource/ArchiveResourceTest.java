package net.kremianskii.zettlekasten.resource;

import net.kremianskii.zettlekasten.ApplicationFixture;
import net.kremianskii.zettlekasten.api.Archive;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import static java.net.http.HttpClient.newHttpClient;
import static java.nio.file.Files.newBufferedWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ArchiveResourceTest extends ApplicationFixture {

    @Test
    void responds_with_200_to_GET_archive() throws InterruptedException, IOException {
        // when
        var client = newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/archive"))
            .timeout(Duration.ofSeconds(2))
            .build();
        var notePath = Paths.get(config.zettlekasten().dir().toString(), "note.md");
        try (var noteWriter = newBufferedWriter(notePath)) {
            noteWriter.append("text");
            noteWriter.newLine();
            noteWriter.append("#tag");
        }
        var response = client.send(request, BodyHandlers.ofString());

        // then
        assertEquals(200, response.statusCode());
        assertEquals(List.of("application/json"), response.headers().allValues("Content-Type"));
        var archive = OBJECT_MAPPER.readValue(response.body(), Archive.class);
        assertNotNull(archive);
        assertEquals(1, archive.notes().size());
        var note = archive.notes().get(0);
        assertEquals(new NoteName("note"), note.name());
        assertEquals("text", note.text());
        assertEquals(Set.of(new Tag("tag")), note.tags());
    }
}
