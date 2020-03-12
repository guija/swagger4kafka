package io.github.stavshamir.swagger4kafka.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class PayloadRef {

    @Getter
    private String $ref;

    private PayloadRef(String $ref) {
        this.$ref = $ref;
    }

    public static PayloadRef fromModelName(String modelName) {
        return new PayloadRef("#/components/schemas/" + modelName);
    }

}
