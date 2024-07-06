package org.wintersleep.crud.provider;

public record SortRequest(
        String fieldName,
        SortDirection direction
) {

    public static SortRequest of(String sort) {
        if (sort == null || sort.isEmpty()) {
            return null;
        }
        String[] parts = sort.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sort string: " + sort);
        }
        return new SortRequest(parts[0], SortDirection.valueOf(parts[1]));
    }

}
