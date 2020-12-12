package com.example.consumer.utils;

import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JacksonUtils {
    public static <T> T fromJson(ObjectMapper objectMapper,
                                 String body,
                                 Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String toJson(ObjectMapper objectMapper, Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
