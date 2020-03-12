package io.github.stavshamir.swagger4kafka.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.swagger.annotations.ApiModel;
import io.swagger.converter.ModelConverters;
import io.swagger.inflector.examples.ExampleBuilder;
import io.swagger.inflector.examples.models.Example;
import io.swagger.inflector.processors.JsonNodeExampleSerializer;
import io.swagger.models.Model;
import io.swagger.models.properties.RefProperty;
import io.swagger.util.Json;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Slf4j
@Service
public class ModelsService {

    private final ModelConverters converter = ModelConverters.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private final Map<String, Model> definitions = new HashMap<>();

    public ModelsService() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule simpleModule = new SimpleModule().addSerializer(new JsonNodeExampleSerializer());
        Json.mapper().registerModule(simpleModule);
    }

    public String register(Class<?> type) {
        log.debug("Registering model for {}", type.getSimpleName());

        Map<String, Model> models = converter.readAll(type);
        fixOriginalRefBug(models.values());

        this.definitions.putAll(models);

        return getModelName(type);
    }

    private void fixOriginalRefBug(Collection<Model> models) {
        /*
         * Replace RefProperty with an extended class which returns a null originalRef to comply with the open api specs.
         * See https://github.com/swagger-api/swagger-core/issues/2944
         * Also, replace definitions in $ref to components/schemas to comply with async api spec.
         */
        models.forEach(model -> model.getProperties().replaceAll((k, v) -> {
            if (v instanceof RefProperty) {
                return new FixedRefProperty(((RefProperty) v).getSimpleRef());
            }

            return v;
        }));
    }

    public Map<String, Object> getExample(String modelName) {
        Model model = definitions.get(modelName);

        if (null == model) {
            log.error("Model for {} was not found", modelName);
            return null;
        }

        Example example = ExampleBuilder.fromModel(modelName, model, definitions, new HashSet<>());
        String exampleAsJson = Json.pretty(example);

        try {
            return objectMapper.readValue(exampleAsJson, Map.class);
        } catch (IOException e) {
            log.error("Failed to convert example object of {} to map", modelName);
            return null;
        }
    }

    private String getModelName(Class<?> type) {
        return Optional
                .ofNullable(type.getAnnotation(ApiModel.class))
                .map(ApiModel::value)
                .orElse(type.getSimpleName());
    }

}
