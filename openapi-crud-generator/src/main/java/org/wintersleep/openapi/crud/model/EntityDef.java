package org.wintersleep.openapi.crud.model;

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

import static java.lang.String.format;

@Getter
public class EntityDef {

    // TODO parent
    private final @NonNull String path;
    private final @NonNull String title;
    private final @NonNull Set<EntityOperationType> operationTypes;
    private final @NonNull Set<AccessAudit> audits;
    private final @NonNull Map<String, PropertyDef> properties;

    public EntityDef(Entity entity) {
        this.path = entity.getPath();
        this.title = entity.getTitle();
        this.operationTypes = Collections.unmodifiableSet(EntityOperationType.parse(entity.getAccess()));
        this.audits = Collections.unmodifiableSet(AccessAudit.parse(entity.getAudit()));
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

    public Schema<?> generateSchema(EntityOperationType operationType, EntityModelType modelType) {
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

    public @NotNull String getModelName(EntityModelType modelType) {
        return switch (modelType) {
            case LIST -> this.title + "Entry";
            case READ -> this.title;
            default -> this.title + StringUtils.capitalize(modelType.name().toLowerCase());
        };
    }

    private Map<String, Schema<?>> generateProperties(EntityModelType modelType, List<PropertyDef> propertyDefs) {
        Preconditions.checkArgument(!propertyDefs.isEmpty(), "Property definitions cannot be empty for {} model of {}", modelType, title);
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
                item.get(buildReadOperation()
                );
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

    private Operation buildListOperation() {
        return new Operation()
                .operationId(operationId(EntityOperationType.LIST))
                .addParametersItem(new QueryParameter()
                                .name("page")
                                .schema(new IntegerSchema()
                                        .minimum(BigDecimal.ZERO))
                        //.required(true)
                )
                .addParametersItem(new QueryParameter()
                                .name("size")
                                .schema(new IntegerSchema()
                                        .minimum(BigDecimal.ONE)
                                        .maximum(BigDecimal.valueOf(100)))
                        //.required(true)
                )
                .addParametersItem(new QueryParameter()
                        .name("sort")
                        .schema(new StringSchema())
                )
                // TODO Filter
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
                                .addHeaderObject("Content-Range", new Header()
                                        .schema(new StringSchema())
                                        .example(format("Content-Range: %s 25-49/123", path))
                                )
                        )
                )
                .extensions(Map.of("x-spring-paginated", true))
                ;
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
        return operationType.name().toLowerCase() + title;
    }

    private static Parameter idParameter() {
        return new PathParameter()
                .name("id")
                .required(true)
                .schema(PrimitiveType.LONG.createProperty());
    }


}
