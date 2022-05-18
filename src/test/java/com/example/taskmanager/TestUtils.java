package com.example.taskmanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class TestUtils {
    public static String toJson(Object object) throws JsonProcessingException {
        var objectMapper = new JsonMapper()
                .registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(object);
    }
}