package org.wintersleep.openapi.crud.sample;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.CompanySortDto;
import org.openapitools.model.EmployeeSortDto;
import org.openapitools.model.UserSortDto;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wintersleep.openapi.crud.core.web.SortConverter;

@Component
@RequiredArgsConstructor
public class SampleWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("http://localhost:5173") // TODO
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .exposedHeaders("X-Total-Count")
        ;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, CompanySortDto.class, new SortConverter<>(CompanySortDto::fromValue));
        registry.addConverter(String.class, EmployeeSortDto.class, new SortConverter<>(EmployeeSortDto::fromValue));
        registry.addConverter(String.class, UserSortDto.class, new SortConverter<>(UserSortDto::fromValue));
    }

}
