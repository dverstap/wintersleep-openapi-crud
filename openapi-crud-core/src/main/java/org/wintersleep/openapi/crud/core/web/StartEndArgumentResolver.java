package org.wintersleep.openapi.crud.core.web;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.wintersleep.openapi.crud.core.provider.StartEnd;

import java.util.function.BiFunction;

@Slf4j
public class StartEndArgumentResolver<T extends StartEnd> implements HandlerMethodArgumentResolver {

    private final Class<T> clazz;
    private final BiFunction<Long, Long, T> converter;

    public StartEndArgumentResolver(Class<T> clazz, BiFunction<Long, Long, T> converter) {
        this.clazz = clazz;
        this.converter = converter;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == clazz;
    }

    @Override
    public T resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                             NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String start = webRequest.getParameter("_start");
        String end = webRequest.getParameter("_end");
        log.debug("Resolving StartEnd argument: [{}-{}[", start, end);
        return converter.apply(
                start == null ? null : Long.parseLong(start),
                end == null ? null : Long.parseLong(end)
        );
    }

}
