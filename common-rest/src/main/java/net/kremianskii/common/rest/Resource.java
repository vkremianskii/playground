package net.kremianskii.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.sun.net.httpserver.HttpServer;
import net.kremianskii.common.rest.http.HttpContextConfigurer;
import net.kremianskii.common.rest.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.kremianskii.common.Checks.checkNonNull;

public abstract class Resource implements HttpContextConfigurer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new Jdk8Module());

    private final List<Route> routes = new ArrayList<>();

    protected void registerRoutes(final List<Route> routes) {
        checkNonNull(routes, "routes");
        this.routes.addAll(routes);
    }

    @Override
    public void configureContexts(final HttpServer server) {
        server.createContext("/", exchange -> {
            final HttpMethod method;
            try {
                method = HttpMethod.valueOf(exchange.getRequestMethod());
            } catch (final IllegalArgumentException exc) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }
            for (final var route : routes) {
                final var matches = route.matches(
                    method,
                    exchange.getRequestURI().getPath());
                if (matches) {
                    try {
                        final var result = route.handler().handle(exchange);
                        if (result == null || (result instanceof Optional<?> optional && optional.isEmpty())) {
                            exchange.sendResponseHeaders(404, -1);
                            exchange.close();
                        } else {
                            try (final var output = exchange.getResponseBody()) {
                                final var bytes = OBJECT_MAPPER.writeValueAsBytes(result);
                                exchange.getResponseHeaders().set("Content-Type", "application/json");
                                exchange.sendResponseHeaders(200, 0);
                                output.write(bytes);
                            }
                        }
                    } catch (final Exception exc) {
                        exchange.sendResponseHeaders(500, -1);
                        exchange.close();
                    }
                    return;
                }
            }
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
    }
}
