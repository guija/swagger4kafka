package io.github.stavshamir.swagger4kafka.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.stavshamir.swagger4kafka.dtos.KafkaEndpoint;
import io.github.stavshamir.swagger4kafka.services.KafkaEndpointsService;
import io.github.stavshamir.swagger4kafka.services.ModelsService;
import io.github.stavshamir.swagger4kafka.types.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpecificationService {

    private final AsyncApiDoc userAsyncApiDoc;
    private final KafkaEndpointsService kafkaEndpointsService;
    private final ModelsService modelsService;

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final ObjectMapper jsonMapper = new ObjectMapper();


    @PostConstruct
    private void postConstruct() {
        yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private AsyncApiDoc getDoc() {
        return AsyncApiDoc.builder()
                .info(getInfo())
                .servers(userAsyncApiDoc.getServers())
                .channels(getChannels())
                .build();
    }

    public String getDocAsJson() throws JsonProcessingException {
       return jsonMapper.writeValueAsString(getDoc());
    }

    public String getSpecificationAsYaml() throws JsonProcessingException {
        return yamlMapper.writeValueAsString(getDoc());
    }

    private Map<String, Channel> getChannels() {
        return kafkaEndpointsService.getEndpoints().stream()
                .collect(toMap(KafkaEndpoint::getTopic, this::getKafkaEndpointChannelFunction));
    }

    private Channel getKafkaEndpointChannelFunction(KafkaEndpoint endpoint) {
        Message message = Message.builder()
                .name(endpoint.getPayloadClassName())
                .title(endpoint.getPayloadModelName())
                .examples(Collections.singletonList(endpoint.getPayloadExample()))
                .payload(modelsService.getDefinitions().get(endpoint.getPayloadModelName()))
                .build();

        Operation op = Operation.builder()
                .message(message)
                .build();

        return new Channel(op);
    }

    private Info getInfo() {
        Info userInfo = userAsyncApiDoc.getInfo();

        return Info.builder()
                .title(userInfo.getTitle())
                .version(userInfo.getVersion())
                .description(userInfo.getDescription())
                .build();
    }

}
