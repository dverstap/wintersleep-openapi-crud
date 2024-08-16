package org.wintersleep.crud.provider;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DataProvider<Entity, ID, EntryDto, SortPropertyId extends Enum<SortPropertyId>, FilterDto, CreateDto, ReadDto, UpdateDto> {

    @Transactional
    default ResponseEntity<List<EntryDto>> list(FilterDto filterDto, SortRequest<SortPropertyId> sortRequest, OffsetLimit offsetLimit) {
        throw new UnsupportedOperationException("list not supported by " + getClass().getSimpleName());
    }

    @Transactional
    default ResponseEntity<ReadDto> create(CreateDto dto) {
        throw new UnsupportedOperationException("create not supported by " + getClass().getSimpleName());
    }

    @Transactional
    ResponseEntity<ReadDto> read(ID id);

    @Transactional
    default ResponseEntity<ReadDto> update(ID id, UpdateDto dto) {
        throw new UnsupportedOperationException("update not supported by " + getClass().getSimpleName());
    }

    @Transactional
    default ResponseEntity<ReadDto> delete(ID id) {
        throw new UnsupportedOperationException("delete not supported by " + getClass().getSimpleName());
    }

}
