package org.wintersleep.openapi.crud.model;

import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record PropertyDef(
        @NonNull String name,
        boolean optional,
        @NonNull Set<PropertyModelType> modelTypes,
        @NonNull String type,
        @Nullable String format
) {

    public boolean required() {
        return !optional;
    }

    public Schema<?> generateSchema() {
        var result = generateSchemaInternal();
        if (result == null) {
            throw new IllegalStateException();
        }
        if (result.getType() == null) {
            throw new IllegalStateException();
        }
        return result;
    }

    private Schema<?> generateSchemaInternal() {
        if (format == null) {
            switch (type) {
                case "int":
                    return PrimitiveType.INT.createProperty();
                case "id":
                case "long":
                    return PrimitiveType.LONG.createProperty();
                case "bigint":
                    return PrimitiveType.INTEGER.createProperty();
                case "decimal":
                    return new StringSchema()
                            .format("decimal");
            }
        }

        // The PrimitiveType is a very Java-centric concept, but good enough for now:
        PrimitiveType primitiveType = PrimitiveType.fromTypeAndFormat(type, format);
        if (primitiveType == null) {
            if (type.equals("string")) {
                return new StringSchema().format(format);
            }
//            return new Schema<>()
//                    //.title(propertyDef.name())
//                    .type(type)
//                    .format(format);
            return PrimitiveType.OBJECT.createProperty();
        }
        return primitiveType.createProperty();
/*
        Schema<?> result = switch (propertyDef.type()) {
            case "number" -> new NumberSchema();
            case "number" -> new NumberSchema();
            case "string" -> switch (propertyDef.format()) {
                case "date" -> new DateSchema();
                case "email" -> new EmailSchema();
                default -> new StringSchema();
            };
            default -> new Schema<>();
        };
        return result
*/
    }

    public static PropertyDef of(String key, String value) {
        String name = key.endsWith("?") ? key.substring(0, key.length() - 1) : key;
        boolean optional = key.endsWith("?");
        String[] parts1 = value.split(" ");
        if (parts1.length != 2) {
            throw new IllegalArgumentException(String.format("Invalid value '%s' for property '%s'", value, name));
        }
        Set<PropertyModelType> accesses = PropertyModelType.parse(parts1[0]);
        String[] parts2 = parts1[1].split(":");
        if (parts2.length > 2) {
            throw new IllegalArgumentException(String.format("Invalid value '%s' for property '%s'", value, name));
        }
        String type = parts2[0];
        String format = parts2.length > 1 ? parts2[1] : null;
        return new PropertyDef(name, optional, accesses, type, format);
    }

    public boolean isIn(PropertyModelType modelType) {
        return modelTypes.contains(modelType);
    }

    public boolean isIn(EntityModelType modelType) {
        return modelTypes.contains(modelType.getPropertyModelType());
    }
}
