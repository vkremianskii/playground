package net.kremianskii.common.rest;

import com.sun.net.httpserver.HttpServer;
import net.kremianskii.common.rest.http.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceTest {

    @Test
    void throws_if_routes_is_null_on_register_routes() {
        // given
        var resource = new Resource() {
        };

        // expect
        assertThrows(IllegalArgumentException.class, () -> resource.registerRoutes(null));
    }

    @Test
    void configures_contexts() throws IOException, InterruptedException {
        // given
        var resource = new Resource() {
        };
        resource.registerRoutes(List.of(new Route(
            HttpMethod.GET,
            "/resource",
            exchange -> new KeyValuePair("value"))));
        resource.registerRoutes(List.of(new Route(
            HttpMethod.POST,
            "/resource",
            exchange -> empty())));
        resource.registerRoutes(List.of(new Route(
            HttpMethod.PUT,
            "/resource",
            exchange -> null)));
        var address = new InetSocketAddress("localhost", 8080);
        var server = HttpServer.create(address, 0);
        var client = HttpClient.newHttpClient();

        // when
        resource.configureContexts(server);
        server.start();
        var getRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/resource"))
            .timeout(Duration.ofSeconds(2))
            .build();
        var postRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/resource"))
            .POST(BodyPublishers.ofString("{}"))
            .timeout(Duration.ofSeconds(2))
            .build();
        var putRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/resource"))
            .PUT(BodyPublishers.ofString("{}"))
            .timeout(Duration.ofSeconds(2))
            .build();
        var deleteRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/resource"))
            .DELETE()
            .timeout(Duration.ofSeconds(2))
            .build();
        var illegalMethodRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/resource"))
            .method("method", BodyPublishers.noBody())
            .timeout(Duration.ofSeconds(2))
            .build();
        var getResponse = client.send(getRequest, BodyHandlers.ofString());
        var postResponse = client.send(postRequest, BodyHandlers.discarding());
        var putResponse = client.send(putRequest, BodyHandlers.discarding());
        var deleteResponse = client.send(deleteRequest, BodyHandlers.discarding());
        var illegalMethodResponse = client.send(illegalMethodRequest, BodyHandlers.discarding());
        server.stop(0);

        // then
        assertEquals(200, getResponse.statusCode());
        assertEquals(List.of("application/json"), getResponse.headers().allValues("Content-Type"));
        assertEquals("{\"key\":\"value\"}", getResponse.body());
        assertEquals(404, postResponse.statusCode());
        assertEquals(404, putResponse.statusCode());
        assertEquals(404, deleteResponse.statusCode());
        assertEquals(400, illegalMethodResponse.statusCode());
    }

    private record KeyValuePair(String key) {
    }
}
