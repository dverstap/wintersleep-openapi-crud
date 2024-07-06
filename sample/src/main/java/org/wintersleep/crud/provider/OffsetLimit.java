package org.wintersleep.crud.provider;

import static java.util.Objects.requireNonNull;

public record OffsetLimit(
        long offset,
        int limit
) {

    public static OffsetLimit of(long offset, int limit) {
        return new OffsetLimit(offset, limit);
    }

    public static OffsetLimit ofPage(Integer page, Integer size) {
        if (page == null && size == null) {
            return null;
        }
        long offset = ((long) requireNonNull(page)) * size;
        return new OffsetLimit(offset, size);
    }

}
