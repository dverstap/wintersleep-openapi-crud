package org.wintersleep.openapi.crud.core.provider;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * The server-side DataProvider mimics the client-side DataProvider:
 * - <a href="https://marmelab.com/react-admin/Architecture.html#providers">React Admin Architecture</a>
 * - <a href="https://marmelab.com/react-admin/DataProviderWriting.html">Writing a DataProvider</a>
 * <p>
 * Crucial to understand is the getList/getMany/getOne operations need to return the same "shape".
 * This is due to how React Admin manages the client-side state:
 * <a href="https://marmelab.com/react-admin/DataProviderWriting.html#getlist-and-getone-shared-cache">getList and getOne Shared Cache</a>
 * I guess this is the same in <a href="https://refine.dev/docs/guides-concepts/data-fetching/">Refine Data Fetching</a>,
 * but it's not explicitly mentioned there.
 * <p>
 * The server-side DataProvider more-or-less replaces the service layer, which it why concrete implementations are
 * typically annotated with @Service.
 * <p>
 * Concrete implementations should also be annotated with @Transactional, otherwise no changes will actually occur.
 */
public interface DataProvider<ID, EntryDto, SortPropertyId extends Enum<SortPropertyId>, FilterDto, CreateDto, ReadDto, UpdateDto> {

    /**
     * Note that for filtering to work, you must register the FilterDto using FilterArgumentResolver,
     * in a WebMvcConfigurer implementation. If you forget this, this will typically snow up as a FilterDto
     * with all properties set to null, even though they are in the HTTP request.
     * For the SortPropertyId, you will need to register a SortConverter in the WebMvcConfigurer.
     * If your entity does not support search, just pass null.
     */
    @Transactional(readOnly = true)
    default ResponseEntity<List<EntryDto>> list(FilterDto filterDto, String search, SortRequest<SortPropertyId> sortRequest, OffsetLimit offsetLimit) {
        throw new UnsupportedOperationException("list not supported by " + getClass().getSimpleName());
    }

    @Transactional(readOnly = true)
    default ResponseEntity<List<ReadDto>> getMany(Collection<Long> ids) {
        throw new UnsupportedOperationException("getMany not supported by " + getClass().getSimpleName());
    }

    @Transactional
    default ResponseEntity<ReadDto> create(CreateDto dto) {
        throw new UnsupportedOperationException("create not supported by " + getClass().getSimpleName());
    }

    @Transactional(readOnly = true)
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
