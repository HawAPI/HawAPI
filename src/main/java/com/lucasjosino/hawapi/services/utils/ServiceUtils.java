package com.lucasjosino.hawapi.services.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtils {

    private final ObjectMapper mapper;

    @Autowired
    public ServiceUtils(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T extends BaseModel> T mergePatch(T model, JsonNode patch, Class<T> classType) throws JsonPatchException, JsonProcessingException {
        JsonNode convertedModel = mapper.valueToTree(model);
        JsonNode mergedNode = JsonMergePatch.fromJson(patch).apply(convertedModel);
        return mapper.treeToValue(mergedNode, classType);
    }
}
