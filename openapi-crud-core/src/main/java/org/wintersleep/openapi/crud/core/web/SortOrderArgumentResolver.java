package org.wintersleep.openapi.crud.core.web;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.wintersleep.openapi.crud.core.provider.SortOrder;

import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public class SortOrderArgumentResolver<
        SortPropertyId extends Enum<SortPropertyId>,
        GeneratedOrderDirection extends Enum<GeneratedOrderDirection>,
        T extends SortOrder<SortPropertyId, GeneratedOrderDirection>
        >
        implements HandlerMethodArgumentResolver {

    private final Class<T> clazz;
    private final Function<String, SortPropertyId> sortConverter;
    private final Function<String, GeneratedOrderDirection> orderConverter;
    private final BiFunction<SortPropertyId, GeneratedOrderDirection, T> converter;

    public SortOrderArgumentResolver(Class<T> clazz,
                                     Function<String, SortPropertyId> sortConverter,
                                     Function<String, GeneratedOrderDirection> orderConverter,
                                     BiFunction<SortPropertyId, GeneratedOrderDirection, T> converter) {
        this.clazz = clazz;
        this.sortConverter = sortConverter;
        this.orderConverter = orderConverter;
        this.converter = converter;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == clazz;
    }

    @Override
    public T resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                             NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String start = webRequest.getParameter("_sort");
        String end = webRequest.getParameter("_order");
        log.debug("Resolving StartEnd argument: [{}-{}[", start, end);
        return converter.apply(
                start == null ? null : sortConverter.apply(start),
                end == null ? null : orderConverter.apply(end)
        );
    }

}
