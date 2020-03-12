package io.github.stavshamir.swagger4kafka.services;

import io.swagger.models.properties.RefProperty;

/**
 * Replace RefProperty with an extended class which returns a null originalRef to comply with the open api specs.
 * See https://github.com/swagger-api/swagger-core/issues/2944.
 *
 * Also, replace definitions in $ref to components/schemas to comply with async api spec.
 */
public class FixedRefProperty extends RefProperty {

    public FixedRefProperty(String ref) {
        super(ref);
    }

    @Override
    public String get$ref() {
        return super.get$ref().replace("definitions", "components/schemas");
    }

    @Override
    public String getOriginalRef() {
        return null;
    }

}
