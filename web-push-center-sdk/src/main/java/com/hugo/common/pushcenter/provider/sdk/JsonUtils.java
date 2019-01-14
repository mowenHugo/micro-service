package com.hugo.common.pushcenter.provider.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public JsonUtils() {
    }

    public static <T> T serializable(String json, Class<T> clazz) throws IOException {
        return StringUtils.isEmpty(json) ? null : mapper.readValue(json, clazz);
    }

    public static <T> T serializable(String json, TypeReference reference) throws IOException {
        return StringUtils.isEmpty(json) ? null : mapper.readValue(json, reference);
    }

    public static String deserializer(Object json) throws JsonProcessingException {
        return json == null ? null : mapper.writeValueAsString(json);
    }

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
