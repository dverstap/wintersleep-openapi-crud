package org.wintersleep.openapi.crud.generator;

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.NonNull;
import org.wintersleep.openapi.crud.model.internal.EnumSpec;
import org.wintersleep.openapi.crud.model.internal.StructSpec;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class StructDef {

    private final @NonNull String name;
    private final @NonNull Map<String, PropertyDef> properties;

    public StructDef(StructSpec struct, Map<String, EnumSpec> enumSpecMap) {
        this.name = struct.getName();
        var properties = new LinkedHashMap<String, PropertyDef>();
        if (struct.getProperties() != null) {
            for (var entry : struct.getProperties().entrySet()) {
                var propertyDef = PropertyDef.ofStruct(entry.getKey(), entry.getValue(), enumSpecMap);
                properties.put(propertyDef.name(), propertyDef);
            }
        }
        this.properties = Collections.unmodifiableMap(properties);
    }

    public Schema<?> generateSchema() {
        if (properties.isEmpty()) {
            return null;
        }
        return (Schema<?>) new Schema<>(SpecVersion.V30)
                .title(name)
                .properties(generateProperties())
                .required(properties.values()
                        .stream()
                        .filter(PropertyDef::required)
                        .map(PropertyDef::name)
                        .toList()
                );
    }

    private Map<String, Schema<?>> generateProperties() {
        Preconditions.checkArgument(!properties.isEmpty(), "Property specs cannot be empty for struct %s", name);
        Map<String, Schema<?>> result = new LinkedHashMap<>();
        for (PropertyDef propertyDef : properties.values()) {
            Schema<?> schema = propertyDef.generateSchema();
            result.put(propertyDef.name(), schema);
        }
        return result;
    }

}
