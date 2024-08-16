package org.wintersleep.openapi.crud.sample.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

import static java.lang.String.format;

@RequiredArgsConstructor
public abstract class AbstractCrudController<
        Entity,
        ID,
        EntryDto,
        FilterDto,
        CreateDto,
        ReadDto,
        UpdateDto> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    protected final String resource;
    protected final JpaRepository<Entity, ID> repository;

    protected ResponseEntity<ReadDto> create(CreateDto dto) {
        Entity entity = mapCreate(dto);
        Entity saved = repository.save(entity);
        ReadDto result = mapRead(saved);
        return ResponseEntity.ok(result);
    }

    protected ResponseEntity<Void> delete(ID id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    protected ResponseEntity<List<EntryDto>> list(FilterDto filterDto, Pageable pageable) {
        log.debug("Filter: {}", filterDto);
        Page<Entity> page = repository.findAll(pageable);
        int start = page.getNumber() * pageable.getPageSize();
        int end = start + page.getNumberOfElements() - 1;
        long total = page.getTotalElements();
        String contentRange = format("%s %d-%d/%d", "users", start, end, total);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_RANGE, contentRange)
                .body(page
                        .stream()
                        .map(this::mapEntry)
                        .toList())
                ;
    }

    protected ResponseEntity<ReadDto> read(ID id) {
        return ResponseEntity.ok(repository.findById(id)
                .map(this::mapRead)
                .orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND, "T with id " + id + " not found")));
    }

    protected ResponseEntity<ReadDto> update(ID id, UpdateDto dto) {
        Entity entity = repository.findById(id)
                .orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND, "T with id " + id + " not found"));
        Entity updated = mapUpdate(dto, entity);
        Entity saved = repository.save(updated);
        ReadDto result = mapRead(saved);
        return ResponseEntity.ok(result);
    }

    protected abstract EntryDto mapEntry(@NonNull Entity entity);

    protected abstract ReadDto mapRead(@NonNull Entity entity);

    protected abstract Entity mapCreate(@NonNull CreateDto dto);

    protected abstract Entity mapUpdate(@NonNull UpdateDto dto, @NonNull Entity entity);

}
