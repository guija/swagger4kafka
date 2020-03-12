package io.github.stavshamir.swagger4kafka.types;

import io.swagger.models.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Components {

    private Map<String, Model> schemas;

}
