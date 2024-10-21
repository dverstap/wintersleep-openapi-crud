package org.wintersleep.openapi.crud.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wintersleep.openapi.crud.core.web.SortOrderArgumentResolver;
import org.wintersleep.openapi.crud.core.web.StartEndArgumentResolver;
import org.wintersleep.openapi.crud.sample.api.model.*;

import java.util.List;

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
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SortOrderArgumentResolver<>(CompanySortOrderDto.class,
                CompanySortPropertyId::fromValue,
                SampleOrderDirection::fromValue,
                (sort, order) -> new CompanySortOrderDto().sort(sort).order(order)));
        resolvers.add(new SortOrderArgumentResolver<>(EmployeeSortOrderDto.class,
                EmployeeSortPropertyId::fromValue,
                SampleOrderDirection::fromValue,
                (sort, order) -> new EmployeeSortOrderDto().sort(sort).order(order)));
        resolvers.add(new SortOrderArgumentResolver<>(UserSortOrderDto.class,
                UserSortPropertyId::fromValue,
                SampleOrderDirection::fromValue,
                (sort, order) -> new UserSortOrderDto().sort(sort).order(order)));

        // only once for the entire openapi spec:
        resolvers.add(new StartEndArgumentResolver<>(SampleStartEndDto.class,
                (start, end) -> new SampleStartEndDto().start(start).end(end)));
    }

/*
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, CompanySortOrderDto.class, new SortConverter<>(CompanySortOrderDto::fromValue));
        registry.addConverter(String.class, EmployeeSortOrderDto.class, new SortConverter<>(EmployeeSortOrderDto::fromValue));
        registry.addConverter(String.class, UserSortOrderDto.class, new SortConverter<>(UserSortOrderDto::fromValue));
    }
*/

}
