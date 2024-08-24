package org.wintersleep.openapi.crud.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.*;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wintersleep.openapi.crud.core.web.FilterArgumentResolver;
import org.wintersleep.openapi.crud.core.web.SortConverter;
import org.wintersleep.openapi.crud.sample.web.util.SortOrderConverter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SampleWebMvcConfigurer implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .exposedHeaders(HttpHeaders.CONTENT_RANGE)
        ;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new FilterArgumentResolver<>(objectMapper, CompanyFilterDto.class));
        resolvers.add(new FilterArgumentResolver<>(objectMapper, EmployeeFilterDto.class));
        resolvers.add(new FilterArgumentResolver<>(objectMapper, UserFilterDto.class));

//        resolvers.add(new SortArgumentResolver<>(CompanySortDto.class, CompanySortDto::fromValue));
//        resolvers.add(new SortArgumentResolver<>(EmployeeSortDto.class, EmployeeSortDto::fromValue));
//        resolvers.add(new SortArgumentResolver<>(UserSortDto.class, UserSortDto::fromValue));
//
//        resolvers.add(new SortOrderArgumentResolver());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, CompanySortDto.class, new SortConverter<>(CompanySortDto::fromValue));
        registry.addConverter(String.class, EmployeeSortDto.class, new SortConverter<>(EmployeeSortDto::fromValue));
        registry.addConverter(String.class, UserSortDto.class, new SortConverter<>(UserSortDto::fromValue));

        registry.addConverter(new SortOrderConverter());
    }

}
