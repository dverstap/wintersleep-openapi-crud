package org.wintersleep.openapi.crud.generator;

import com.google.common.base.Preconditions;
import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.SpecVersion;
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
import org.wintersleep.openapi.crud.model.internal.Entity;

import java.math.BigDecimal;
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

    public EntityDef(Entity entity) {
        this.path = entity.getPath();
        this.title = entity.getTitle();
        this.pluralTitle = entity.getPluralTitle();
        this.operationTypes = Collections.unmodifiableSet(EntityOperationType.parse(entity.getAccess()));
        this.audits = Collections.unmodifiableSet(AccessAudit.parse(entity.getAudit()));
        this.search = entity.getSearch();
        var properties = new LinkedHashMap<String, PropertyDef>();
        if (entity.getProperties() != null) {
            for (var entry : entity.getProperties().entrySet()) {
                var propertyDef = PropertyDef.of(entry.getKey(), entry.getValue());
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

    public Schema<?> generateSortSchema() {
        String title = getModelName(PropertyModelType.SORT);
        return new Schema<>(SpecVersion.V30)
                .title(title)
                // x-implements doesn't do anything for enums
                //.extensions(Map.of("x-implements", "org.wintersleep.crud.provider.SortEnum"))
                ._enum(getSortableProperties());
    }

    public @NotNull String getModelName(EntityModelType modelType) {
        return getModelName(modelType.getPropertyModelType());
    }

    public @NotNull String getModelName(PropertyModelType modelType) {
        return switch (modelType) {
            case SORT -> this.title + "Sort";
            case LIST -> this.title + "Entry";
            case READ -> this.title;
            default -> this.title + StringUtils.capitalize(modelType.name().toLowerCase());
        };
    }

    private Map<String, Schema<?>> generateProperties(EntityModelType modelType, List<PropertyDef> propertyDefs) {
        Preconditions.checkArgument(!propertyDefs.isEmpty(), "Property definitions cannot be empty for {} model of {}", modelType, title);
        Map<String, Schema<?>> result = new LinkedHashMap<>();
//        if (modelType == EntityModelType.FILTER) {
//            result.put("q", new StringSchema());
//        }
        for (PropertyDef propertyDef : propertyDefs) {
            Schema<?> schema = propertyDef.generateSchema();
            result.put(propertyDef.name(), schema);
        }
// TODO:
//        for (AccessAudit audit : audits) {
//        }
        return result;
    }

    public void addPaths(Paths paths) {
        if (supportsList() || supportsCreate()) {
            PathItem item = new PathItem();
            if (supportsList()) {
                item.get(buildListOperation());
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
        if (supportsRead()) {
            PathItem item = new PathItem();
            item.get(buildGetManyOperation());
            paths.addPathItem("/" + path + "/ids/{ids}", item);
        }
    }

    private Operation buildListOperation() {
        Operation operation = new Operation()
                .operationId(operationId(EntityOperationType.LIST))
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
                            .name("_sort")
                            .schema(new Schema<>()
                                    .$ref("#/components/schemas/" + getModelName(PropertyModelType.SORT)))
                    )
                    .addParametersItem(new QueryParameter()
                            .name("_order")
                            .schema(new Schema<>()
                                    .$ref("#/components/schemas/SortOrder"))
                    );
        }
        operation
                .addParametersItem(new QueryParameter()
                                .name("_start")
                                .schema(new IntegerSchema()
                                        .format("int64")
                                        .minimum(BigDecimal.ZERO))
                        //.required(true)
                )
                .addParametersItem(new QueryParameter()
                                .name("_end")
                                .schema(new IntegerSchema()
                                        .format("int64")
                                        .minimum(BigDecimal.ONE)
                                        .maximum(BigDecimal.valueOf(1000)))
                        //.required(true)
                );
        operation
                .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                                .description("OK")
                                                .content(new Content()
                                                        .addMediaType("application/json", new MediaType()
                                                                .schema(new ArraySchema()
                                                                        .items(new Schema<>()
                                                                                .$ref("#/components/schemas/" + getModelName(EntityModelType.LIST))
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

    private Operation buildGetManyOperation() {
        return new Operation()
                .operationId(operationId(EntityOperationType.GET_MANY))
                .addParametersItem(idsParameter())
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

    private static Parameter idParameter() {
        return new PathParameter()
                .name("id")
                .required(true)
                .schema(PrimitiveType.LONG.createProperty());
    }

/*
    private static Parameter idsParameter() {
        return new QueryParameter()
                .name("id")
                .required(true)
                .schema(new ArraySchema()
                        .items(PrimitiveType.LONG.createProperty()));
    }
*/

    private static Parameter idsParameter() {
        return new PathParameter()
                .name("ids")
                .required(true)
                .schema(new ArraySchema()
                        .items(PrimitiveType.LONG.createProperty()));
    }

/*
    private static Parameter idsParameter() {
        return new PathParameter()
                .name("ids")
                .required(true)
                .schema(new StringSchema());
    }
*/

}
