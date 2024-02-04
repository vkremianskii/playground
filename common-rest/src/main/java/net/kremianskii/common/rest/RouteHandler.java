package net.kremianskii.common.rest;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

@FunctionalInterface
public interface RouteHandler {
    Object handle(HttpExchange exchange) throws IOException;
}
