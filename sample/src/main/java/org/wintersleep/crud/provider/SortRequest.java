package org.wintersleep.crud.provider;

import java.util.function.Function;

public record SortRequest<T extends Enum<T>>(
        T propertyId,
        SortDirection direction
) {

    public static <T extends Enum<T>> SortRequest<T> parse(String sort, Function<String, T> enumConverter) {
        if (sort == null || sort.isEmpty()) {
            return null;
        }
        String[] parts = sort.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sort string: " + sort);
        }
        T propertyId = enumConverter.apply(parts[0]);
        SortDirection direction = SortDirection.valueOf(parts[1]);
        return new SortRequest<>(propertyId, direction);
    }

}
