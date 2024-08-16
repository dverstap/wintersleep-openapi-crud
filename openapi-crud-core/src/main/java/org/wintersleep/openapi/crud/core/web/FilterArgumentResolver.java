package org.wintersleep.openapi.crud.core.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FilterArgumentResolver<T> implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;
    private final Class<T> clazz;

    public FilterArgumentResolver(ObjectMapper objectMapper, Class<T> clazz) {
        this.objectMapper = objectMapper;
        this.clazz = clazz;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == clazz;
    }

    @Override
    public T resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String filter = webRequest.getParameter("filter");
        if (filter == null) {
            return clazz.getConstructor().newInstance();
        }
        return objectMapper.readValue(filter, clazz);
    }
}
