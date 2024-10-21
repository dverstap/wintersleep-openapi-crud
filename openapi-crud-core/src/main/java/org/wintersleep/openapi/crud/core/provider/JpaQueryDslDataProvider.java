package org.wintersleep.openapi.crud.core.provider;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@Getter
public abstract class JpaQueryDslDataProvider<
        Entity,
        ID,
        SortPropertyId extends Enum<SortPropertyId>,
        GeneratedOrderDirection extends Enum<GeneratedOrderDirection>,
        FilterDto,
        CreateDto,
        ReadDto,
        UpdateDto
        >
        implements DataProvider<ID, SortPropertyId, GeneratedOrderDirection, FilterDto, CreateDto, ReadDto, UpdateDto> {

    protected final Logger log;
    protected final String resource;
    protected final Class<Entity> entityClass;
    protected final EntityPath<Entity> entityPath;
    protected final NumberPath<Long> idPath;

    @PersistenceContext
    protected EntityManager entityManager;

    public JpaQueryDslDataProvider(String resource, Class<Entity> entityClass, EntityPath<Entity> entityPath, NumberPath<Long> idPath) {
        this.log = LoggerFactory.getLogger(getClass() + "." + resource);
        this.resource = resource;
        this.entityClass = entityClass;
        this.entityPath = entityPath;
        this.idPath = idPath;
    }

    @Override
    public ResponseEntity<List<ReadDto>> list(List<Long> ids, FilterDto filterDto, String search, SortOrder<SortPropertyId, GeneratedOrderDirection> sortOrder, StartEnd startEnd) {
        if (ids != null && !ids.isEmpty()) {
            return getMany(ids);
        } else {
            return getList(filterDto, search, OrderBy.of(sortOrder), OffsetLimit.of(startEnd));
        }
    }

    protected ResponseEntity<List<ReadDto>> getList(FilterDto filterDto, String search, OrderBy<SortPropertyId> orderBy, OffsetLimit offsetLimit) {
        log.debug("Filter: {}", filterDto);
        BooleanExpression where = mapWhere(filterDto, search);
        JPAQuery<Entity> query = baseQuery()
                .where(where);
        if (orderBy != null) {
            query
                    .orderBy(mapOrderSpecifier(orderBy));
        }
        if (offsetLimit != null) {
            query
                    .offset(offsetLimit.offset())
                    .limit(offsetLimit.limit());
        }
        // TODO notice the restrictions on count in the deprecation message!
        QueryResults<Entity> results = query.fetchResults();
        List<Entity> page = results.getResults();
        return ResponseEntity
                .ok()
                .header("X-Total-Count", Long.toString(results.getTotal()))
                .body(page
                        .stream()
                        .map(this::mapRead)
                        .toList())
                ;
    }

    protected JPAQuery<Entity> baseQuery() {
        return new JPAQuery<>(entityManager)
                .select(entityPath)
                .from(entityPath);
    }

    protected ResponseEntity<List<ReadDto>> getMany(Collection<Long> ids) {
        BooleanExpression where = idPath.in(ids);
        JPAQuery<Entity> query = new JPAQuery<>(entityManager)
                .select(entityPath)
                .from(entityPath)
                .where(where);
        List<Entity> entities = query.fetch();
        return ResponseEntity
                .ok()
                .body(entities
                        .stream()
                        .map(this::mapRead)
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

    @Override
    //@Transactional
    public ResponseEntity<ReadDto> create(CreateDto dto) {
        Entity entity = mapCreate(dto);
        entityManager.persist(entity);
        ReadDto result = mapRead(entity);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<ReadDto> read(ID id) {
        Entity entity = find(id);
        ReadDto result = mapRead(entity);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<ReadDto> update(ID id, UpdateDto dto) {
        Entity entity = find(id);
        Entity updated = mapUpdate(dto, entity);
        ReadDto result = mapRead(updated);
        return ResponseEntity.ok(result);
    }

    @Override
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

    protected BooleanExpression mapWhere(FilterDto filterDto, String search) {
        return Expressions.allOf(
                filterDto == null ? null : mapFilter(filterDto),
                StringUtils.hasText(search) ? mapSearch(search) : null
        );
    }

    protected abstract BooleanExpression mapFilter(FilterDto filterDto);

    protected abstract BooleanExpression mapSearch(String search);

    protected OrderSpecifier<? extends Comparable<?>> mapOrderSpecifier(OrderBy<SortPropertyId> orderBy) {
        Order order = orderBy.direction() == OrderDirection.ASC ? Order.ASC : Order.DESC;
        Expression<? extends Comparable<?>> expression = mapOrderExpression(orderBy);
        OrderSpecifier.NullHandling nullhandling = mapOrderNullHandling(orderBy);
        return new OrderSpecifier<>(order, expression, nullhandling);
    }

    protected Expression<? extends Comparable<?>> mapOrderExpression(OrderBy<SortPropertyId> orderBy) {
        return mapOrderExpression(orderBy.propertyId(), orderBy.direction());
    }

    protected abstract Expression<? extends Comparable<?>> mapOrderExpression(SortPropertyId propertyId, OrderDirection direction);

    protected OrderSpecifier.NullHandling mapOrderNullHandling(OrderBy<SortPropertyId> orderBy) {
        // TODO make the default configurable:
        return OrderSpecifier.NullHandling.Default;
    }

    protected BooleanExpression like(StringPath path, String value) {
        if (value == null) {
            return null;
        }
        return path.likeIgnoreCase("%" + value + "%");
    }

    protected <T extends Comparable<?>> BooleanExpression eq(SimpleExpression<T> path, T value) {
        if (value == null) {
            return null;
        }
        return path.eq(value);
    }

    protected abstract ReadDto mapRead(@NonNull Entity entity);

    protected abstract Entity mapCreate(@NonNull CreateDto dto);

    protected abstract Entity mapUpdate(@NonNull UpdateDto dto, @NonNull Entity entity);

}
