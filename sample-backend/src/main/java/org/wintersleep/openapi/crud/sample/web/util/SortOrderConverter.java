package org.wintersleep.openapi.crud.sample.web.util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openapitools.model.SortOrder;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class SortOrderConverter implements Converter<String, SortOrder> {

    @Override
    public SortOrder convert(@NotNull String source) {
        return SortOrder.fromValue(source);
    }

}
