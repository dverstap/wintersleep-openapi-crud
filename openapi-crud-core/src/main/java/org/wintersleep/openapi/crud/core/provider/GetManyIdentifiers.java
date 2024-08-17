package org.wintersleep.openapi.crud.core.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

public interface GetManyIdentifiers {

    static SequencedSet<Long> parse(String value) {
        SequencedSet<Long> ids = Arrays.stream(tokenizeToStringArray(value, "_"))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return Collections.unmodifiableSequencedSet(ids);
    }

}
