package net.kremianskii.zettlekasten.resource;

import net.kremianskii.zettlekasten.ApplicationFixture;
import net.kremianskii.zettlekasten.api.Archive;
import net.kremianskii.zettlekasten.api.Note;
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

class ArchiveResourceTest extends ApplicationFixture {

    @Test
    void responds_with_200_to_GET_archive() throws InterruptedException, IOException {
        // when
        var client = newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/archive"))
            .timeout(Duration.ofSeconds(2))
            .build();
        var notePath = Paths.get(zettlekastenDirectory.toString(), "note.md");
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
        assertEquals(
            new Archive(List.of(new Note(
                new NoteName("note"),
                "text",
                Set.of(new Tag("tag"))))),
            archive);
    }
}
