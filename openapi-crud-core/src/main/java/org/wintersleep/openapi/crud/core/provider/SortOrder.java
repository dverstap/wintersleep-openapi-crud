package org.wintersleep.openapi.crud.core.provider;

public interface SortOrder<SortPropertyId extends Enum<SortPropertyId>, GeneratedOrderDirection extends Enum<GeneratedOrderDirection>> {

    SortPropertyId getSort();

    GeneratedOrderDirection getOrder();
}
