package org.wintersleep.openapi.crud.core.provider;

import java.util.function.Function;

public record SortRequest<PropertyId extends Enum<PropertyId>>(
        PropertyId propertyId,
        SortDirection direction
) {

    public static <PropertyId extends Enum<PropertyId>, SO extends Enum<SO>> SortRequest<PropertyId> of(PropertyId propertyId, SO order) {
        if (propertyId == null) {
            return null;
        }
        SortDirection direction = order == null ? SortDirection.ASC : SortDirection.valueOf(order.name());
        return new SortRequest<>(propertyId, direction);
    }

    public static <PropertyId extends Enum<PropertyId>> SortRequest<PropertyId> parse(String sort, Function<String, PropertyId> enumConverter) {
        if (sort == null || sort.isEmpty()) {
            return null;
        }
        String[] parts = sort.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sort string: " + sort);
        }
        PropertyId propertyId = enumConverter.apply(parts[0]);
        SortDirection direction = SortDirection.valueOf(parts[1]);
        return new SortRequest<>(propertyId, direction);
    }

}
