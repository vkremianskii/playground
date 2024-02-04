package net.kremianskii.common.rest.http;

import com.sun.net.httpserver.HttpServer;

public interface HttpContextConfigurer {
    void configureContexts(final HttpServer server);
}
