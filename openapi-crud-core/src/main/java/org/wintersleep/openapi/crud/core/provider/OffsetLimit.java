package org.wintersleep.openapi.crud.core.provider;

public record OffsetLimit(
        long offset,
        int limit
) {
    private static final int DEFAULT_LIMIT = 100;

    public static OffsetLimit of(long offset, int limit) {
        return new OffsetLimit(offset, limit);
    }

    public static OffsetLimit ofPage(Integer page, Integer size) {
        if (page == null && size == null) {
            return null;
        }
        if (page == null) {
            throw new IllegalArgumentException("page must not be null");
        }
        int pageSize = size == null ? DEFAULT_LIMIT : size;
        long offset = ((long) page) * pageSize;
        return new OffsetLimit(offset, pageSize);
    }

}
