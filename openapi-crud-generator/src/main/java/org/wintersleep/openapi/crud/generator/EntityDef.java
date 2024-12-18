package org.wintersleep.openapi.crud.generator;

import com.google.common.base.Preconditions;
import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.wintersleep.openapi.crud.model.internal.EntitySpec;
import org.wintersleep.openapi.crud.model.internal.EnumSpec;

import java.util.*;

@Getter
public class EntityDef {

    // TODO parent
    private final @NonNull String path;
    private final @NonNull String title;
    private final @NonNull String pluralTitle;
    private final @NonNull Set<EntityOperationType> operationTypes;
    private final @NonNull Set<AccessAudit> audits;
    private final boolean search;
    private final @NonNull Map<String, PropertyDef> properties;

    public EntityDef(EntitySpec entitySpec, Map<String, EnumSpec> enums, Map<String, StructDef> structs) {
        this.path = entitySpec.getPath();
        this.title = entitySpec.getTitle();
        this.pluralTitle = entitySpec.getPluralTitle();
        this.operationTypes = Collections.unmodifiableSet(EntityOperationType.parse(entitySpec.getOperations()));
        this.audits = Collections.unmodifiableSet(AccessAudit.parse(entitySpec.getAudit()));
        this.search = entitySpec.getSearch();
        var properties = new LinkedHashMap<String, PropertyDef>();
        if (entitySpec.getProperties() != null) {
            for (var entry : entitySpec.getProperties().entrySet()) {
                var propertyDef = PropertyDef.ofEntity(entry.getKey(), entry.getValue(), enums, structs);
                properties.put(propertyDef.name(), propertyDef);
            }
        }
        this.properties = Collections.unmodifiableMap(properties);
    }

    public boolean supportsList() {
        return operationTypes.contains(EntityOperationType.LIST);
    }

    public boolean supportsCreate() {
        return operationTypes.contains(EntityOperationType.CREATE);
    }

    public boolean supportsRead() {
        return operationTypes.contains(EntityOperationType.READ);
    }

    public boolean supportsUpdate() {
        return operationTypes.contains(EntityOperationType.UPDATE);
    }

    public boolean supportsDelete() {
        return operationTypes.contains(EntityOperationType.DELETE);
    }

    public List<PropertyDef> getPropertyDefs(EntityModelType modelType) {
        return properties
                .values()
                .stream()
                .filter(def -> def.isIn(modelType))
                .toList()
                ;
    }

    public List<PropertyDef> getPropertyDefs(PropertyModelType modelType) {
        return properties
                .values()
                .stream()
                .filter(def -> def.isIn(modelType))
                .toList()
                ;
    }

    public Schema<?> generateSchema(EntityModelType modelType) {
        String title = getModelName(modelType);
        List<PropertyDef> propertyDefs = getPropertyDefs(modelType);
        if (propertyDefs.isEmpty()) {
            return null;
        }
        Schema<?> schema = new Schema<>(SpecVersion.V30)
                .title(title)
                .properties(generateProperties(modelType, propertyDefs));
        if (modelType != EntityModelType.FILTER) {
            return schema
                    .required(propertyDefs
                            .stream()
                            .filter(def -> def.isIn(modelType))
                            .filter(PropertyDef::required)
                            .map(PropertyDef::name)
                            .toList()
                    );
        }
        return schema;
    }

    public Schema<?> generateSortOrderSchema(String orderDirectionSchemaName, String javaPackageName) {
        String title = getModelName(PropertyModelType.SORT);
        // Make sure that these two are excluded from adding the Dto suffix,
        // by putting them in the <modelNameMappings> in the pom.xml file:
        String sortClassName = javaPackageName + "." + getSortPropertyIdTitle();
        String orderClassName = javaPackageName + "." + orderDirectionSchemaName;
        //System.out.println(sortClassName + ": " + orderClassName);
        return new ObjectSchema()
                .title(title)
                .addProperty("_sort", new Schema<>().$ref("#/components/schemas/" + getSortPropertyIdTitle()))
                .addProperty("_order", new Schema<>().$ref("#/components/schemas/" + orderDirectionSchemaName))
                .extensions(Map.of(
                        "x-implements",
                        "org.wintersleep.openapi.crud.core.provider.SortOrder<%s, %s>".formatted(sortClassName, orderClassName)
                ))
                ;
    }

    public Schema<?> generateSortPropertyIdSchema() {
        String title = getSortPropertyIdTitle();
        return new Schema<>(SpecVersion.V30)
                .title(title)
                // x-implements doesn't do anything for enums
                //.extensions(Map.of("x-implements", "org.wintersleep.crud.provider.SortEnum"))
                ._enum(getSortableProperties());
    }

    private @NotNull String getSortPropertyIdTitle() {
        return this.title + "SortPropertyId";
    }

    public @NotNull String getModelName(EntityModelType modelType) {
        return getModelName(modelType.getPropertyModelType());
    }

    public @NotNull String getModelName(PropertyModelType modelType) {
        return switch (modelType) {
            case SORT -> this.title + "SortOrder";
            case READ -> this.title;
            default -> this.title + StringUtils.capitalize(modelType.name().toLowerCase());
        };
    }

    private Map<String, Schema<?>> generateProperties(EntityModelType modelType, List<PropertyDef> propertyDefs) {
        Preconditions.checkArgument(!propertyDefs.isEmpty(), "Property specs cannot be empty for %s model of %s", modelType, title);
        Map<String, Schema<?>> result = new LinkedHashMap<>();
        for (PropertyDef propertyDef : propertyDefs) {
            Schema<?> schema = propertyDef.generateSchema();
            result.put(propertyDef.name(), schema);
        }
// TODO:
//        for (AccessAudit audit : audits) {
//        }
        return result;
    }

    public void addPaths(String startEndSchemaName, Paths paths) {
        if (supportsList() || supportsCreate()) {
            PathItem item = new PathItem();
            if (supportsList()) {
                item.get(buildListOperation(startEndSchemaName));
            }
            if (supportsCreate()) {
                item.post(buildCreateOperation());
            }
            paths.addPathItem("/" + path, item);
        }
        if (supportsRead() || supportsUpdate() || supportsDelete()) {
            PathItem item = new PathItem();
            if (supportsRead()) {
                item.get(buildReadOperation());
            }
            if (supportsUpdate()) {
                item.put(buildUpdateOperation());
            }
            if (supportsDelete()) {
                item.delete(buildDeleteOperation());
            }
            paths.addPathItem("/" + path + "/{id}", item);
        }
    }

    private Operation buildListOperation(String startEndSchemaName) {
        Operation operation = new Operation()
                .operationId(operationId(EntityOperationType.LIST))
                .addParametersItem(idArrayParameter())
                .addParametersItem(new QueryParameter()
                        .name("filter")
                        // https://swagger.io/specification/v3/#parameter-object:
                        .style(Parameter.StyleEnum.FORM)
                        // .explode(true) // is the default for style=form
                        .schema(new Schema<>()
                                .$ref("#/components/schemas/" + getModelName(EntityModelType.FILTER)))
                );
        if (search) {
            operation.addParametersItem(new QueryParameter()
                    .name("q")
                    .schema(new StringSchema())
            );
        }
        if (!getSortableProperties().isEmpty()) {
            operation
                    .addParametersItem(new QueryParameter()
                            .name("sort-order")
                            // https://swagger.io/specification/v3/#parameter-object:
                            .style(Parameter.StyleEnum.FORM)
                            // .explode(true) // is the default for style=form
                            .schema(new Schema<>()
                                    .$ref("#/components/schemas/" + getModelName(PropertyModelType.SORT)))
                    );
//                    .addParametersItem(new QueryParameter()
//                            .name("_sort")
//                            .schema(new Schema<>()
//                                    .$ref("#/components/schemas/" + getModelName(PropertyModelType.SORT)))
//                    )
//                    .addParametersItem(new QueryParameter()
//                            .name("_order")
//                            .schema(new Schema<>()
//                                    .$ref("#/components/schemas/" + sortOrderSchemaName))
//                    );
        }
        operation
                .addParametersItem(new QueryParameter()
                        .name("start-end")
                        // https://swagger.io/specification/v3/#parameter-object:
                        .style(Parameter.StyleEnum.FORM)
                        // .explode(true) // is the default for style=form
                        .schema(new Schema<>()
                                .$ref("#/components/schemas/" + startEndSchemaName))
                );
        operation
                .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                                .description("OK")
                                                .content(new Content()
                                                        .addMediaType("application/json", new MediaType()
                                                                .schema(new ArraySchema()
                                                                        .items(new Schema<>()
                                                                                .$ref("#/components/schemas/" + getModelName(EntityModelType.READ))
                                                                        )
                                                                )
                                                        )
                                                )
                                                // The React Admin ra-data-simple-rest provider uses Content-Range:
                                                //   https://github.com/marmelab/react-admin/tree/master/packages/ra-data-simple-rest#cors-setup
                                                // The Refine simple-rest provider uses: x-total-count:
                                                //   https://refine.dev/docs/data/data-provider/#retrieving-the-total-row-count
                                                //   https://github.com/refinedev/refine/blob/master/packages/simple-rest/src/provider.ts
                                                // In either case, if API and UI are on different hosts, make sure CORS is set up correctly,
                                                // as explained in the React Admin docs.
//                                .addHeaderObject("Content-Range", new Header()
//                                        .schema(new StringSchema())
//                                        .example(format("Content-Range: %s 25-49/123", path))
//                                )
                                                // The React Admin ra-data-json-server uses X-Total-Count:
                                                //   https://github.com/marmelab/react-admin/tree/master/packages/ra-data-json-server#rest-dialect
                                                .addHeaderObject("X-Total-Count", new Header()
                                                        .schema(new StringSchema())
                                                        .example("X-Total-Count: 319"))
                                )
                );
        return operation
                //.extensions(Map.of("x-spring-paginated", true))
                ;
    }

    public List<String> getSortableProperties() {
        return getPropertyDefs(PropertyModelType.SORT)
                .stream()
                .map(PropertyDef::name)
                .toList();
    }

    private Operation buildCreateOperation() {
        return new Operation()
                .operationId(operationId(EntityOperationType.CREATE))
                .requestBody(new RequestBody()
                        .required(true)
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new Schema<>()
                                                .$ref("#/components/schemas/" + getModelName(EntityModelType.CREATE))
                                        )
                                )
                        )
                )
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description("OK")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(new Schema<>()
                                                        .$ref("#/components/schemas/" + getModelName(EntityModelType.READ))
                                                )
                                        )
                                )
                        )
                );
    }

    private Operation buildReadOperation() {
        return new Operation()
                .operationId(operationId(EntityOperationType.READ))
                .addParametersItem(idParameter())
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description("OK")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(new Schema<>()
                                                        .$ref("#/components/schemas/" + getModelName(EntityModelType.READ))
                                                )
                                        )
                                )
                        )
                );
    }

    private Operation buildUpdateOperation() {
        return new Operation()
                .operationId(operationId(EntityOperationType.UPDATE))
                .addParametersItem(idParameter())
                .requestBody(new RequestBody()
                        .required(true)
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new Schema<>()
                                                .$ref("#/components/schemas/" + getModelName(EntityModelType.UPDATE))
                                        )
                                )
                        )
                )
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description("OK")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(new Schema<>()
                                                        .$ref("#/components/schemas/" + getModelName(EntityModelType.READ))
                                                )
                                        )
                                )
                        )
                );
    }

    private Operation buildDeleteOperation() {
        return new Operation()
                .operationId(operationId(EntityOperationType.DELETE))
                .addParametersItem(idParameter())
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description("OK")
                        )
                );
    }

    private String operationId(EntityOperationType operationType) {
        // Unfortunately, this must be unique, preventing making an abstract class with default implementations ...
        // That is one of the reasons to introduce the DataProvider interface server-side as well
        String prefix = operationType == EntityOperationType.GET_MANY ? "getMany" : operationType.name().toLowerCase();
        if (operationType.isMultiple()) {
            return prefix + pluralTitle;
        } else {
            return prefix + title;
        }
    }

    public void addComponents(String orderDirectionSchemaName, String javaPackageName, Components components) {
        for (EntityOperationType operationType : operationTypes) {
            for (EntityModelType modelType : operationType.getModelTypes()) {
                Schema<?> schema = generateSchema(modelType);
                if (schema != null) {
                    components.addSchemas(schema.getTitle(), schema);
                }
            }
            List<String> sortableProperties = getSortableProperties();
            if (!sortableProperties.isEmpty()) {
                Schema<?> sortOrderSchema = generateSortOrderSchema(orderDirectionSchemaName, javaPackageName);
                components
                        .addSchemas(sortOrderSchema.getTitle(), sortOrderSchema);
                Schema<?> sortFieldSchema = generateSortPropertyIdSchema();
                components
                        .addSchemas(sortFieldSchema.getTitle(), sortFieldSchema);
            }
        }
    }

    private Parameter idParameter() {
        PropertyDef id = properties.get("id");
        if (id != null) {
            return new PathParameter()
                    .name("id")
                    .required(true)
                    .schema(id.generateSchema());
        }
        return new PathParameter()
                .name("id")
                .required(true)
                .schema(PrimitiveType.LONG.createProperty());
    }

    private Parameter idArrayParameter() {
        PropertyDef id = properties.get("id");
        if (id != null) {
            return new QueryParameter()
                    .name("id")
                    .schema(new ArraySchema()
                            .items(id.generateSchema()));
        }
        return new QueryParameter()
                .name("id")
                .schema(new ArraySchema()
                        .items(PrimitiveType.LONG.createProperty()));
    }

}
