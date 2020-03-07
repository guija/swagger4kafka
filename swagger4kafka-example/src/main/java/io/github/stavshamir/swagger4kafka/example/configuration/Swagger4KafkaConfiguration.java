package io.github.stavshamir.swagger4kafka.example.configuration;

import com.google.common.collect.ImmutableMap;
import io.github.stavshamir.swagger4kafka.configuration.EnableSwagger4Kafka;
import io.github.stavshamir.swagger4kafka.configuration.KafkaProtocolConfiguration;
import io.github.stavshamir.swagger4kafka.types.AsyncApiDoc;
import io.github.stavshamir.swagger4kafka.types.Info;
import io.github.stavshamir.swagger4kafka.types.Server;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
@EnableSwagger4Kafka
public class Swagger4KafkaConfiguration {

    private final String BOOTSTRAP_SERVERS;

    public Swagger4KafkaConfiguration(@Value("${kafka.bootstrap.servers}") String bootstrapServers) {
        this.BOOTSTRAP_SERVERS = bootstrapServers;
    }

    @Bean
    public AsyncApiDoc userAsyncApiDoc() {
        Info info = Info.builder()
                .title("Async API For Spring Boot Example Project")
                .description("An example of the AsyncApi specification")
                .version("1.0.0")
                .build();

        return AsyncApiDoc.builder()
                .info(info)
                .servers(Server.kafkaBootstrapServers(BOOTSTRAP_SERVERS))
                .build();
    }


    @Bean
    public KafkaProtocolConfiguration kafkaProtocolConfiguration() {
        return KafkaProtocolConfiguration.builder()
                .basePackage("io.github.stavshamir.swagger4kafka.example.consumers")
                .producerConfiguration(producerConfiguration())
                .bootstrapServers(BOOTSTRAP_SERVERS)
                .build();
    }

    private Map<String, Object> producerConfiguration() {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
                .put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false)
                .build();
    }

}
