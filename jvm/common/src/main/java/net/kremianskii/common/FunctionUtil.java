package net.kremianskii.common;

import java.util.function.Consumer;
import java.util.function.Function;

public final class FunctionUtil {

    private FunctionUtil() {
    }

    public static <T> Consumer<T> unchecked(ThrowingConsumer<T> consumer) {
        return arg -> {
            try {
                consumer.accept(arg);
            } catch (final Exception exc) {
                throw new RuntimeException(exc);
            }
        };
    }

    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> function) {
        return arg -> {
            try {
                return function.apply(arg);
            } catch (final Exception exc) {
                throw new RuntimeException(exc);
            }
        };
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
