package io.github.stavshamir.swagger4kafka.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Server {

    private String url;
    private String protocol;
    private String protocolVersion;
    private String description;

    public static Server kafkaBootstrapServer(String url) {
        return Server.builder()
                .protocol("kafka")
                .url(url)
                .build();
    }

    public static Map<String, Server> kafkaBootstrapServers(String bootstrapServers) {
        return Arrays.stream(bootstrapServers.split(","))
                .collect(toMap(Server::buildName, Server::kafkaBootstrapServer));
    }

    private static String buildName(String url) {
        return "kafka_" + url;
    }
}
