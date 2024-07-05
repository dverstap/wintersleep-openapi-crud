package org.wintersleep.crud.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractCrudController<
        Entity,
        ID,
        EntryDto,
        CreateDto,
        ReadDto,
        UpdateDto> {

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

    protected ResponseEntity<List<EntryDto>> list(Pageable pageable) {
        return ResponseEntity.ok(repository.findAll(pageable)
                .stream()
                .map(this::mapEntry)
                .toList());
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
