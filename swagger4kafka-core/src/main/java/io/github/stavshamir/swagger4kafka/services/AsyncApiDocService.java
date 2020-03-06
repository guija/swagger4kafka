package io.github.stavshamir.swagger4kafka.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.stavshamir.swagger4kafka.types.AsyncApiDoc;
import io.github.stavshamir.swagger4kafka.types.Info;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncApiDocService {

    private final AsyncApiDoc userAsyncApiDoc;
    private final KafkaListenersScanner kafkaListenersScanner;

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Getter
    private AsyncApiDoc doc;

    @Getter
    private String docAsYaml;

    @PostConstruct
    private void postConstruct() throws JsonProcessingException {
        yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        doc = buildDoc();
        docAsYaml = yamlMapper.writeValueAsString(doc);
    }

    private AsyncApiDoc buildDoc() {
        return AsyncApiDoc.builder()
                .info(getInfo())
                .servers(userAsyncApiDoc.getServers())
                .channels(kafkaListenersScanner.getChannels())
                .build();
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
