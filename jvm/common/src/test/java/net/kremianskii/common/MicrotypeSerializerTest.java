package net.kremianskii.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kremianskii.common.MicrotypeFixtures.FloatMicrotype;
import net.kremianskii.common.MicrotypeFixtures.IntegerMicrotype;
import net.kremianskii.common.MicrotypeFixtures.StringMicrotype;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MicrotypeSerializerTest {

    @Test
    void serializes_microtype() throws JsonProcessingException {
        // given
        var mapper = new ObjectMapper();

        // expect
        assertEquals("1", mapper.writeValueAsString(new IntegerMicrotype(1)));
        assertEquals("1.0", mapper.writeValueAsString(new FloatMicrotype(1.0f)));
        assertEquals("\"value\"", mapper.writeValueAsString(new StringMicrotype("value")));
    }
}
