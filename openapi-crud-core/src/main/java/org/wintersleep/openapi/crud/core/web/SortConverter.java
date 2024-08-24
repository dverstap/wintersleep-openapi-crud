package org.wintersleep.openapi.crud.core.web;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

import java.util.function.Function;

// Even if you register a customer HandlerMethodArgumentResolver for a specific enum,
// Spring MVC converts them using the built-in EnumToStringConverter.
// The reason is explained in: https://github.com/spring-projects/spring-framework/issues/23043
// which refers to the Javadoc:
//   https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurationSupport.html#addArgumentResolvers-java.util.List-
// Instead, we need to use a converter.
// Note that OpenAPI Processor can generate these converters automatically:
//   https://openapiprocessor.io/spring/processor/enums.html
// and only a single ConverterFactory must be registered.
@Slf4j
public class SortConverter<T extends Enum<T>> implements Converter<String, T> {

    private final Function<String, T> converter;

    public SortConverter(Function<String, T> converter) {
        this.converter = converter;
    }

    @Override
    public T convert(@NotNull String source) {
        return converter.apply(source);
    }

}
