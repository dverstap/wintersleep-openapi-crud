package org.wintersleep.openapi.crud.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.v3.core.util.ObjectMapperFactory;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.wintersleep.openapi.crud.model.internal.Entity;
import org.wintersleep.openapi.crud.model.internal.OpenapiCrudSchema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Generator {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Expected two arguments: <INPUT_FILE> <OUTPUT_FILE>");
            System.exit(1);
        }
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        OpenAPI openAPI = new OpenAPI()
                .openapi("3.0.3");
        openAPI.setInfo(new Info()
                .version("0.0.1")
                .title("Test API")
                .description("Test API") // Not required, but avoids false error from openapi generator
        );
        openAPI.addServersItem(
                new Server()
                        .url("http://localhost:8080")
        );

        ObjectMapper mapper = ObjectMapperFactory.createYaml();
        JsonNode schemaNode = schemaNode(mapper);
        //validateSchema(schemaNode);

        JsonNode node = mapper.readTree(inputFile);
        validateLegacy(schemaNode, node);
        //validate(node);

        ObjectMapper dataMapper = new ObjectMapper(new YAMLFactory());
        OpenapiCrudSchema crudSchema = dataMapper.readValue(inputFile, OpenapiCrudSchema.class);
        Paths paths = new Paths();
        Components components = new Components();
        for (Entity entity : crudSchema.getEntities()) {
            EntityDef entityDef = new EntityDef(entity);
            entityDef.addPaths(paths);
            for (EntityOperationType access : entityDef.getOperationTypes()) {
                for (EntityModelType modelType : access.getModelTypes()) {
                    Schema<?> schema = entityDef.generateSchema(modelType);
                    components.addSchemas(schema.getTitle(), schema);
                }
                List<String> sortableProperties = entityDef.getSortableProperties();
                if (!sortableProperties.isEmpty()) {
                    Schema<?> schema = entityDef.generateSortSchema();
                    components.addSchemas(schema.getTitle(), schema);
                }
            }
        }
        openAPI.paths(paths);
        openAPI.components(components);

        System.out.flush();
        mapper.writeValue(outputFile, openAPI);
        //mapper.writeValue(System.out, openAPI); // warning: closes System.out!
    }

    private static void validateLegacy(JsonNode schemaNode, JsonNode node) {
        org.everit.json.schema.Schema schema = parseSchema(schemaNode);
        String str = node.toPrettyString();
        //System.out.println(str);
        JSONObject object = new JSONObject(new JSONTokener(str));
        try {
            schema.validate(object); // throws a ValidationException if this object is invalid
        } catch (ValidationException e) {
            for (String message : e.getAllMessages()) {
                System.err.println(message);
            }
            System.exit(2);
        }
    }

    private static org.everit.json.schema.Schema parseSchema(JsonNode schemaNode) {
        String str = schemaNode.toPrettyString();
        //System.out.println(str);
        JSONObject rawSchema = new JSONObject(new JSONTokener(str));
        return SchemaLoader.load(rawSchema);
    }

    // Does not seem to detect field type errors nor
/*
    private static void validate(JsonNode node) {
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

        SchemaValidatorsConfig.Builder builder = SchemaValidatorsConfig.builder();
// By default the JDK regular expression implementation which is not ECMA 262 compliant is used
// Note that setting this requires including optional dependencies
// builder.regularExpressionFactory(GraalJSRegularExpressionFactory.getInstance());
// builder.regularExpressionFactory(JoniRegularExpressionFactory.getInstance());
        SchemaValidatorsConfig config = builder.build();

// Due to the mapping the schema will be retrieved from the classpath at classpath:schema/example-main.json.
// If the schema data does not specify an $id the absolute IRI of the schema location will be used as the $id.
        JsonSchema schema = jsonSchemaFactory.getSchema(SchemaLocation.of("https://wintersleep.org/openapi-crud.schema.yml"), config);
        validate(node, schema);
    }

    private static void validateSchema(JsonNode schemaNode) throws IOException {
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        JsonSchema schema = jsonSchemaFactory.getSchema(SchemaLocation.of(SchemaId.V202012));
        validate(schemaNode, schema);
    }

    private static void validate(JsonNode node, JsonSchema schema) {
        Set<ValidationMessage> assertions = schema.validate(node, executionContext -> {
            // By default since Draft 2019-09 the format keyword only generates annotations and not assertions
            executionContext.getExecutionConfig().setFormatAssertionsEnabled(true);
        });
        for (ValidationMessage message : assertions) {
            System.err.println(message);
        }
        if (!assertions.isEmpty()) {
            System.exit(3);
        }
    }
*/

    private static JsonNode schemaNode(ObjectMapper mapper) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String name = "schema/openapi-crud.schema.yml";
        try (InputStream inputStream = classLoader.getResourceAsStream(name)) {
            return mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing: " + name, e);
        }
    }

}