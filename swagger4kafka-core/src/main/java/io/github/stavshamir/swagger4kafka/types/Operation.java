package io.github.stavshamir.swagger4kafka.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    private Map<String, OperationBinding> bindings;
    private Message message;
}
