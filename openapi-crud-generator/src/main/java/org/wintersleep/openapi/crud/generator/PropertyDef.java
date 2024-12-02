package org.wintersleep.openapi.crud.generator;

import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wintersleep.openapi.crud.model.internal.EnumDefinition;

import java.util.Map;
import java.util.Set;

public record PropertyDef(
        @NonNull String name,
        boolean optional,
        boolean array,
        @NonNull Set<PropertyModelType> modelTypes,
        @NonNull String type,
        @Nullable String format,
        @Nullable EnumDefinition enumDefinition,
        @Nullable StructDef structDef
) {

    public boolean required() {
        return !optional;
    }

    public Schema<?> generateSchema() {
        var result = generatePrimitiveSchema();
        if (result == null) {
            throw new IllegalStateException();
        }
        if (result.getType() == null || result.get$ref() == null) {
            //throw new IllegalStateException();
        }
        if (array) {
            return new ArraySchema()
                    .items(result);
        }
        return result;
    }

    private Schema<?> generatePrimitiveSchema() {
        if (enumDefinition != null) {
            return new Schema<>()
                    .$ref("#/components/schemas/" + enumDefinition.getName());
        }
        if (structDef != null) {
            return new Schema<>()
                    .$ref("#/components/schemas/" + structDef.getName());
        }
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

    static PropertyDef ofEntity(String key, String value) {
        return ofEntity(key, value, Map.of(), Map.of());
    }

    public static PropertyDef ofEntity(String key, String value,
                                       Map<String, EnumDefinition> enums,
                                       Map<String, StructDef> structs) {
        Name name = Name.parse(key);
        String[] parts1 = value.split(" ");
        if (parts1.length != 2) {
            throw new IllegalArgumentException("Expected two space-separated words instead of '%s' for property '%s'"
                    .formatted(value, name.name()));
        }
        String propertyCodes = parts1[0];
        String invalidCodes = PropertyModelType.findInvalidCodes(propertyCodes);
        if (!invalidCodes.isEmpty()) {
            throw new IllegalArgumentException("Invalid PropertyModelType codes '%s' in '%s' for property '%s'"
                    .formatted(invalidCodes, value, name.name()));
        }
        Set<PropertyModelType> propertyModelTypes = PropertyModelType.parse(propertyCodes);
        Type typeResult = Type.parse(name, parts1[1], value);
        return new PropertyDef(
                name.name(), name.optional(), name.array(), propertyModelTypes, typeResult.type(), typeResult.format(),
                typeResult.format() != null ? enums.get(typeResult.format()) : null,
                structs.get(typeResult.type())
        );
    }

    public static PropertyDef ofStruct(String key, String value,
                                       Map<String, EnumDefinition> enums) {
        Name name = Name.parse(key);
        Type typeResult = Type.parse(name, value, null);
        return new PropertyDef(name.name(), name.optional(), name.array(), Set.of(), typeResult.type(), typeResult.format(),
                typeResult.format() != null ? enums.get(typeResult.format()) : null,
                null // nested structs not supported at the moment
        );
    }

    public boolean isIn(PropertyModelType modelType) {
        return modelTypes.contains(modelType);
    }

    public boolean isIn(EntityModelType modelType) {
        return modelTypes.contains(modelType.getPropertyModelType());
    }

    private record Name(boolean optional, boolean array, String name) {
        static Name parse(String key) {
            boolean optional = key.endsWith("?");
            boolean array = key.endsWith("[]");
            String name;
            if (optional) {
                name = key.substring(0, key.length() - 1);
            } else if (array) {
                name = key.substring(0, key.length() - 2);
            } else {
                name = key;
            }
            return new Name(optional, array, name);
        }
    }


    private record Type(String type, String format) {
        private static Type parse(Name result, String value, String entityPropertyValue) {
            String[] parts = value.split(":");
            if (parts.length > 2) {
                if (entityPropertyValue != null) {
                    throw new IllegalArgumentException("Expected 'type' or 'type:format' in the second word of '%s' for property '%s'"
                            .formatted(entityPropertyValue, result.name()));
                } else {
                    throw new IllegalArgumentException("Expected 'type' or 'type:format' in the value '%s' for property '%s'"
                            .formatted(value, result.name()));
                }
            }
            String type = parts[0];
            String format = parts.length > 1 ? parts[1] : null;
            return new Type(type, format);
        }
    }


}
