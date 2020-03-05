package io.github.stavshamir.swagger4kafka.producer;

import com.google.common.collect.ImmutableMap;
import io.github.stavshamir.swagger4kafka.configuration.KafkaProtocolConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "async-api", name = "protocols.kafka")
public class KafkaProducer {

    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaProtocolConfiguration configuration) {
        Map<String, Object> config = config(configuration);

        DefaultKafkaProducerFactory<String, Map<String, Object>> factory = new DefaultKafkaProducerFactory<>(config);
        this.kafkaTemplate = new KafkaTemplate<>(factory);
    }

    private Map<String, Object> config(KafkaProtocolConfiguration configuration) {
        return Optional.ofNullable(configuration.getProducerConfiguration())
                .orElseGet(() -> defaultConfig(configuration.getBootstrapServers()));
    }

    private Map<String, Object> defaultConfig(String bootstrapServers) {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
                .build();
    }

    public void send(String topic, Map<String, Object> payload) {
        kafkaTemplate.send(topic, payload);
    }

}
