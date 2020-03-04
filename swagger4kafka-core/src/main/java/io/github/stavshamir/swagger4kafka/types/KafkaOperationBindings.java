package io.github.stavshamir.swagger4kafka.types;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaOperationBindings implements OperationBinding {

    private String groupId;

}
