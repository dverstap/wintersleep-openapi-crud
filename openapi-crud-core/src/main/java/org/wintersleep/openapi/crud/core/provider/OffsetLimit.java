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
        int limit = size == null ? DEFAULT_LIMIT : size;
        long offset = ((long) page) * limit;
        return new OffsetLimit(offset, limit);
    }

    public static OffsetLimit ofStartEnd(Long start, Long end) {
        if (start == null && end == null) {
            return null;
        }
        if (start == null) {
            throw new IllegalArgumentException("start must not be null");
        }
        if (end != null && start > end) {
            throw new IllegalArgumentException("start (%s) must be less than end (%s)".formatted(start, end));
        }
        long offset = start;
        int limit = end == null ? DEFAULT_LIMIT : (int) (end - start);
        return new OffsetLimit(offset, limit);
    }

    public static OffsetLimit of(StartEnd startEnd) {
        return ofStartEnd(startEnd.getStart(), startEnd.getEnd());
    }

}
