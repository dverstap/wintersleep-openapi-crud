package org.wintersleep.crud.provider;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static java.lang.String.format;

//@Transactional
@Getter
public abstract class AbstractDataProvider<
        Entity,
        ID,
        EntryDto,
        FilterDto,
        CreateDto,
        ReadDto,
        UpdateDto> {

    protected final Logger log;
    protected final String resource;
    protected final Class<Entity> entityClass;
    protected final EntityPath<Entity> entityPath;

    @PersistenceContext
    protected EntityManager entityManager;

    public AbstractDataProvider(String resource, Class<Entity> entityClass, EntityPath<Entity> entityPath) {
        this.log = LoggerFactory.getLogger(getClass() + "." + resource);
        this.resource = resource;
        this.entityClass = entityClass;
        this.entityPath = entityPath;
    }

    //@Transactional
    public ResponseEntity<List<EntryDto>> list(FilterDto filterDto, SortRequest sortRequest, OffsetLimit offsetLimit) {
        log.debug("Filter: {}", filterDto);
        BooleanExpression where = filterDto == null ? null : mapFilter(filterDto);
        JPAQuery<Entity> query = new JPAQuery<>(entityManager)
                .select(entityPath)
                .from(entityPath)
                .where(where)
                // TODO .orderBy()
                ;
        if (offsetLimit != null) {
            query
                    .offset(offsetLimit.offset())
                    .limit(offsetLimit.limit());
        }
        // TODO notice the restrictions on count in the deprecation message!
        QueryResults<Entity> results = query.fetchResults();
        List<Entity> page = results.getResults();
        String contentRange = contentRange(results);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_RANGE, contentRange)
                .body(page
                        .stream()
                        .map(this::mapEntry)
                        .toList())
                ;
    }

    protected String contentRange(QueryResults<Entity> results) {
        List<Entity> page = results.getResults();
        long start = results.getOffset();
        long end = start + page.size() - 1;
        long total = results.getTotal();
        return format("%s %d-%d/%d", resource, start, end, total);
    }

    public ResponseEntity<ReadDto> create(CreateDto dto) {
        Entity entity = mapCreate(dto);
        entityManager.persist(entity);
        ReadDto result = mapRead(entity);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<ReadDto> read(ID id) {
        Entity entity = find(id);
        ReadDto result = mapRead(entity);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<ReadDto> update(ID id, UpdateDto dto) {
        Entity entity = find(id);
        Entity updated = mapUpdate(dto, entity);
        ReadDto result = mapRead(updated);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<ReadDto> delete(ID id) {
        Entity entity = find(id);
        entityManager.remove(entity);
        ReadDto result = mapRead(entity);
        return ResponseEntity.ok(result);
    }

    protected Entity find(ID id) {
        Entity entity = entityManager.find(entityPath.getType(), id);
        if (entity == null) {
            // We throw an exception, so the transaction is rolled back
            String msg = format("%s with id %s not found", entityClass.getSimpleName(), id);
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND, msg);
        }
        return entity;
    }

    protected abstract BooleanExpression mapFilter(@NonNull FilterDto filterDto);

    protected BooleanExpression like(StringPath path, String value) {
        if (value == null) {
            return null;
        }
        return path.likeIgnoreCase("%" + value + "%");
    }

    protected abstract EntryDto mapEntry(@NonNull Entity entity);

    protected abstract ReadDto mapRead(@NonNull Entity entity);

    protected abstract Entity mapCreate(@NonNull CreateDto dto);

    protected abstract Entity mapUpdate(@NonNull UpdateDto dto, @NonNull Entity entity);

}