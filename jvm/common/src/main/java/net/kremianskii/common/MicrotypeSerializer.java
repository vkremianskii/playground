package net.kremianskii.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public final class MicrotypeSerializer extends JsonSerializer<Microtype<?>> {
    @Override
    public void serialize(Microtype<?> microtype,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writePOJO(microtype.value);
    }
}
