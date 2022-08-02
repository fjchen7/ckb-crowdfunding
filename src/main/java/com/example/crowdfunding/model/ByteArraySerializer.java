package com.example.crowdfunding.model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nervos.ckb.utils.Numeric;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class ByteArraySerializer {
    public static class Serializer extends JsonSerializer<byte[]> {
        @Override
        public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            System.out.println("serialize--:" + value);
            gen.writeString(Numeric.toHexString(value).toLowerCase());
        }
    }

    public static class Deserializer extends JsonDeserializer<byte[]> {
        @Override
        public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            System.out.println("deserialize--:" + p.getValueAsString());
            return Numeric.hexStringToByteArray(p.getValueAsString());
        }
    }
}
