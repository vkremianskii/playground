package net.kremianskii.zettlekasten.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class HttpZettlekastenClientTest {

    static ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new Jdk8Module());

    HttpClient httpClient = mock(HttpClient.class);

    @Test
    void finds_archive_returns_an_archive() throws InterruptedException, IOException {
        // given
        var client = new HttpZettlekastenClient(URI.create("http://localhost:8080"), httpClient, objectMapper);
        var response = mock(HttpResponse.class);
        given(httpClient.send(any(), any())).willReturn(response);
        given(response.statusCode()).willReturn(200);
        given(response.body()).willReturn("""
            {
                "notes": [{
                    "name": {
                        "value": "name"
                    },
                    "text": "text",
                    "tags": [{
                        "value": "tag"
                    }]
                }]
            }
            """.getBytes());

        // when
        var archive = client.findArchive();

        // then
        assertTrue(archive.isPresent());
    }

    @Test
    void find_archive_returns_empty_if_response_status_code_is_404() throws InterruptedException, IOException {
        // given
        var client = new HttpZettlekastenClient(URI.create("http://localhost:8080"), httpClient, objectMapper);
        var response = mock(HttpResponse.class);
        given(httpClient.send(any(), any())).willReturn(response);
        given(response.statusCode()).willReturn(404);

        // when
        var archive = client.findArchive();

        // then
        assertTrue(archive.isEmpty());
    }
}
