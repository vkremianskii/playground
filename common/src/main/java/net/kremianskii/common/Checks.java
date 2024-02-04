package net.kremianskii.common;

import java.util.function.Predicate;

public final class Checks {

    private Checks() {
    }

    public static <T> T checkNonNull(final T value, final String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return value;
    }

    public static String checkNonEmpty(final String value, final String name) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return value;
    }

    public static <T> T checkThat(final T value,
                                  final Predicate<T> condition,
                                  final String message) {
        if (!condition.test(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
