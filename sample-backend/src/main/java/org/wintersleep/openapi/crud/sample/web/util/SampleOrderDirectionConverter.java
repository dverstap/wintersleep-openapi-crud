package org.wintersleep.openapi.crud.sample.web.util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.wintersleep.openapi.crud.sample.api.model.SampleOrderDirection;

@Slf4j
public class SampleOrderDirectionConverter implements Converter<String, SampleOrderDirection> {

    @Override
    public SampleOrderDirection convert(@NotNull String source) {
        return SampleOrderDirection.fromValue(source);
    }

}
