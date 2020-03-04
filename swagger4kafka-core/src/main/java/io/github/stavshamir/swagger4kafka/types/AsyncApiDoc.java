package io.github.stavshamir.swagger4kafka.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncApiDoc {

    @Builder.Default
    private String asyncapi = "2.0.0";

    private Info info;

    @Builder.Default
    private Map<String, Server> servers = new HashMap<>();

    @Builder.Default
    private Map<String, Channel> channels = new HashMap<>();

}
