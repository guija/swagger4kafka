package io.github.stavshamir.swagger4kafka.configuration;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class KafkaProtocolConfiguration {
    /**
     * Define the base package in which @KafkaListener annotations will be scanned (required).
     */
    private final String basePackage;

    /**
     * Define the bootstrap servers to be used for publishing messages (optional - defaults to localhost:9092).
     */
    @Builder.Default
    private final String bootstrapServers = "localhost:9092";

    /**
     * Provide configuration properties for the Kafka producer (optional).
     * <p>
     * If this field is not set, the following default configuration is used:
     *     <ul>
     *         <li><code>ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> </code>bootstrap servers set in the docket, or default</li>
     *         <li><code>ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> StringSerializer.class</code></li>
     *         <li><code>ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> JsonSerializer.class</code></li>
     *     </ul>
     * </p>
     */
    private final Map<String, Object> producerConfiguration;

}
