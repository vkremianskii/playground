package net.kremianskii.common.rest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.kremianskii.common.rest.http.HttpMethod;

import java.util.function.Function;

import static net.kremianskii.common.Checks.checkNonEmpty;
import static net.kremianskii.common.Checks.checkNonNull;

public record Route(HttpMethod method,
                    String path,
                    RouteHandler handler) {
    public Route {
        checkNonNull(method, "method");
        checkNonEmpty(path, "path");
        checkNonNull(handler, "handler");
    }

    public boolean matches(HttpMethod method, String path) {
        checkNonNull(method, "method");
        checkNonEmpty(path, "path");
        return method == this.method && path.equals(this.path);
    }
}
