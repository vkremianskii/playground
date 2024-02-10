package net.kremianskii.common;

import net.kremianskii.common.FunctionUtil.ThrowingConsumer;
import net.kremianskii.common.FunctionUtil.ThrowingFunction;
import org.junit.jupiter.api.Test;

import static net.kremianskii.common.FunctionUtil.unchecked;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FunctionUtilTest {

    @Test
    void convert_checked_exceptions_thrown_by_consumer_to_unchecked() {
        // expect
        var consumer = unchecked((ThrowingConsumer<Integer>) arg -> {
            throw new Exception();
        });
        assertThrows(RuntimeException.class, () -> consumer.accept(0));
    }

    @Test
    void convert_checked_exceptions_thrown_by_function_to_unchecked() {
        // expect
        var consumer = unchecked((ThrowingFunction<Integer, Integer>) arg -> {
            throw new Exception();
        });
        assertThrows(RuntimeException.class, () -> consumer.apply(0));
    }
}
