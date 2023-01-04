package com.lucasjosino.hawapi.services.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.google.common.base.CaseFormat;
import com.lucasjosino.hawapi.filters.base.BaseFilter;
import com.lucasjosino.hawapi.models.base.BaseModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtils {

    private final ObjectMapper mapper;

    private final ExampleMatcher match;

    private final ModelMapper modelMapper;

    @Autowired
    public ServiceUtils(ObjectMapper mapper, ExampleMatcher match, ModelMapper modelMapper) {
        this.mapper = mapper;
        this.match = match;
        this.modelMapper = modelMapper;
    }

    public <T extends BaseModel> T mergePatch(
            T model,
            JsonNode patch,
            Class<T> classType
    ) throws JsonPatchException, JsonProcessingException {
        JsonNode convertedModel = mapper.valueToTree(model);
        JsonNode mergedNode = JsonMergePatch.fromJson(patch).apply(convertedModel);
        return mapper.treeToValue(mergedNode, classType);
    }

    public <T extends BaseModel, S extends BaseFilter> Example<T> filter(S filter, Class<T> classType) {
        T convertedModel = modelMapper.map(filter, classType);
        return Example.of(convertedModel, match);
    }

    public <T extends BaseFilter> Sort buildSort(T filter) {
        if (filter.getOrder() == null || filter.getOrder().isEmpty()) return null;

        if (filter.getSort() == null || filter.getSort().isEmpty()) {
            filter.setSort("ASC");
        }

        // Workaround for https://github.com/spring-projects/spring-data-rest/issues/1638
        filter.setOrder(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, filter.getOrder()));

        Sort.Direction sort = Sort.Direction.fromString(filter.getSort());
        return Sort.by(sort, filter.getOrder());
    }
}
