package org.wintersleep.openapi.crud.core.provider;

import java.util.function.Function;

public record OrderBy<PropertyId extends Enum<PropertyId>>(
        PropertyId propertyId,
        OrderDirection direction
) {

    public static <PropertyId extends Enum<PropertyId>, SO extends Enum<SO>> OrderBy<PropertyId> of(PropertyId propertyId, SO order) {
        if (propertyId == null) {
            return null;
        }
        OrderDirection direction = order == null ? OrderDirection.ASC : OrderDirection.valueOf(order.name());
        return new OrderBy<>(propertyId, direction);
    }

    public static <PropertyId extends Enum<PropertyId>> OrderBy<PropertyId> parse(String sort, Function<String, PropertyId> enumConverter) {
        if (sort == null || sort.isEmpty()) {
            return null;
        }
        String[] parts = sort.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sort string: " + sort);
        }
        PropertyId propertyId = enumConverter.apply(parts[0]);
        OrderDirection direction = OrderDirection.valueOf(parts[1]);
        return new OrderBy<>(propertyId, direction);
    }

    public static <SortPropertyId extends Enum<SortPropertyId>, GeneratedOrderDirection extends Enum<GeneratedOrderDirection>> OrderBy<SortPropertyId> of(SortOrder<SortPropertyId, GeneratedOrderDirection> sortOrder) {
        if (sortOrder == null) {
            return null;
        }
        if (sortOrder.getSort() == null) {
            return null;
        }
        OrderDirection direction = sortOrder.getOrder() == null
                ? OrderDirection.ASC
                : OrderDirection.valueOf(sortOrder.getOrder().name());
        return OrderBy.of(sortOrder.getSort(), direction);
    }

}
