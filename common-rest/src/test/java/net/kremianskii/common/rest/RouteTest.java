package net.kremianskii.common.rest;

import net.kremianskii.common.rest.http.HttpMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteTest {

    @Test
    void throws_if_method_is_null_in_ctor() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Route(null, "path", exchange -> null));
    }

    @Test
    void throws_if_name_is_null_or_empty_in_ctor() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Route(HttpMethod.GET, null, exchange -> null));
        assertThrows(
            IllegalArgumentException.class,
            () -> new Route(HttpMethod.GET, "", exchange -> null));
    }

    @Test
    void throws_if_handler_is_null() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Route(HttpMethod.GET, "path", null));
    }
}
