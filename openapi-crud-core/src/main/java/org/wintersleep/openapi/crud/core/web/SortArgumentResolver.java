package org.wintersleep.openapi.crud.core.web;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.function.Function;

@Slf4j
public class SortArgumentResolver<T extends Enum<T>> implements HandlerMethodArgumentResolver {

    private final Class<T> clazz;
    private final Function<String, T> converter;

    public SortArgumentResolver(Class<T> clazz, Function<String, T> converter) {
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
        String sort = webRequest.getParameter("sort");
        if (sort == null) {
            return null;
        }
        log.debug("Resolving sort argument: {}", sort);
        return converter.apply(sort);
    }

}
