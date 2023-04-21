package com.lucasjosino.hawapi.services.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ServiceUtils {

    private final ObjectMapper mapper;

    @Autowired
    public ServiceUtils(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T, Y> T merge(T model, Y dto) throws IOException {
        return mapper.readerForUpdating(model).readValue((JsonNode) mapper.valueToTree(dto));
    }
}