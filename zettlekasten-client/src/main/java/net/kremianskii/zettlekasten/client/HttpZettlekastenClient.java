package net.kremianskii.zettlekasten.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kremianskii.zettlekasten.api.Archive;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import static java.util.Optional.empty;
import static net.kremianskii.common.Checks.checkNonNull;

public class HttpZettlekastenClient implements ZettlekastenClient {
    private final URI baseUri;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpZettlekastenClient(URI baseUri,
                                  HttpClient httpClient,
                                  ObjectMapper objectMapper) {
        this.baseUri = checkNonNull(baseUri, "baseUri");
        this.httpClient = checkNonNull(httpClient, "httpClient");
        this.objectMapper = checkNonNull(objectMapper, "objectMapper");
    }

    @Override
    public Optional<Archive> findArchive() {
        final var uri = URI.create(baseUri.toString() + "/archive");
        final var request = HttpRequest.newBuilder(uri).build();
        try {
            final var response = httpClient.send(request, BodyHandlers.ofByteArray());
            if (response.statusCode() == 404) {
                return empty();
            }
            if (response.statusCode() != 200) {
                throw new RuntimeException("Expected status code 200, was %d".formatted(response.statusCode()));
            }
            return Optional.of(objectMapper.readValue(response.body(), Archive.class));
        } catch (final InterruptedException | IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}
