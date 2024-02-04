package net.kremianskii.zettlekasten.resource;

import net.kremianskii.zettlekasten.ApplicationFixture;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import static java.net.http.HttpClient.newHttpClient;
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
        var response = client.send(request, BodyHandlers.ofString());

        // then
        assertEquals(200, response.statusCode());
    }
}
