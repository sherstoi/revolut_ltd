package com.revolut.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Created by iurii on 7/21/17.
 */
public class JSONValueDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken jsonToken = p.getCurrentToken();
        if (jsonToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
            return p.getValueAsString();
        }
        return null;
    }
}
