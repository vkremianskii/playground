package net.kremianskii.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kremianskii.common.MicrotypeFixtures.FloatMicrotype;
import net.kremianskii.common.MicrotypeFixtures.IntegerMicrotype;
import net.kremianskii.common.MicrotypeFixtures.StringMicrotype;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MicrotypeTest {

    @Test
    void throws_if_value_is_null_in_ctor() {
        // expect
        assertThrows(IllegalArgumentException.class, () -> new StringMicrotype(null));
        assertDoesNotThrow(() -> new StringMicrotype("value"));
    }

    @Test
    void to_string_returns_value_converted_to_string() {
        // given
        int value = 1;

        // when
        var microtype = new IntegerMicrotype(value);

        // expect
        assertEquals(Integer.toString(value), microtype.toString());
    }

    @Test
    void implements_equals() {
        // given
        var microtype1 = new StringMicrotype("value");
        var microtype2 = new StringMicrotype("value");

        // expect
        assertEquals(microtype1, microtype2);
    }

    @Test
    void implements_hashcode() {
        // given
        var microtype1 = new StringMicrotype("value1");
        var microtype2 = new StringMicrotype("value2");
        var microtype3 = new StringMicrotype("value1");
        var map = new HashMap<Microtype<?>, Integer>();
        List.of(microtype1, microtype2, microtype3).forEach(microtype ->
            map.compute(microtype, (k, v) -> v != null ? v + 1 : 1));

        // expect
        assertEquals(2, map.size());
        assertEquals(2, map.get(microtype1));
        assertEquals(1, map.get(microtype2));
    }

    @Test
    void be_deserialized_from_value() throws JsonProcessingException {
        // given
        var mapper = new ObjectMapper();

        // expect
        assertEquals(
            new IntegerMicrotype(1),
            mapper.readValue("1", IntegerMicrotype.class));
        assertEquals(
            new FloatMicrotype(1.0f),
            mapper.readValue("1.0", FloatMicrotype.class));
        assertEquals(
            new StringMicrotype("value"),
            mapper.readValue("\"value\"", StringMicrotype.class));
    }
}
