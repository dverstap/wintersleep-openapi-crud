package org.wintersleep.openapi.crud.core.web;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Type;
import java.util.List;

public class GetManyIdsArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.getMethod().getName().startsWith("getMany")) {
            return false;
        }
        if (parameter.getParameterType() != List.class) {
            return false;
        }
        if (!"ids".equals(parameter.getParameterName())) {
            return false;
        }
        Type type = parameter.getGenericParameterType();
        System.out.println(type);
        // TODO
        return false;
    }

    @Override
    public List<Long> resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        throw new UnsupportedOperationException();
    }

}
