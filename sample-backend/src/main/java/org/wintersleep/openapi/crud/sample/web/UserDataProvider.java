package org.wintersleep.openapi.crud.sample.web;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.NonNull;
import org.openapitools.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wintersleep.openapi.crud.core.provider.JpaQueryDslDataProvider;
import org.wintersleep.openapi.crud.core.provider.OrderDirection;
import org.wintersleep.openapi.crud.sample.domain.QUser;
import org.wintersleep.openapi.crud.sample.domain.User;

@Service
@Transactional
public class UserDataProvider extends JpaQueryDslDataProvider<
        User, Long,
        UserSortPropertyId, SampleOrderDirection, UserFilterDto,
        UserCreateDto, UserDto, UserUpdateDto> {

    public UserDataProvider() {
        super("users", User.class, QUser.user, QUser.user.id);
    }

    @Override
    protected BooleanExpression mapFilter(UserFilterDto dto) {
        QUser user = QUser.user;
        return Expressions.allOf(
                like(user.email, dto.getEmail()),
                like(user.firstName, dto.getFirstName()),
                like(user.lastName, dto.getLastName()),
                like(user.displayName, dto.getDisplayName())
        );
    }

    @Override
    protected BooleanExpression mapSearch(String q) {
        QUser user = QUser.user;
        return Expressions.anyOf(
                like(user.email, q),
                like(user.firstName, q),
                like(user.lastName, q),
                like(user.displayName, q)
        );
    }


    @Override
    protected Expression<? extends Comparable<?>> mapOrderExpression(UserSortPropertyId propertyId, OrderDirection direction) {
        final QUser user = QUser.user;
        return switch (propertyId) {
            case ID -> user.id;
            case EMAIL -> user.email;
            case FIRST_NAME -> user.firstName;
            case LAST_NAME -> user.lastName;
            case DISPLAY_NAME -> user.displayName;
        };
    }

    protected UserDto mapRead(@NonNull User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .displayName(user.getDisplayName())
                .build();
    }

    protected User mapCreate(@NonNull UserCreateDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .displayName(dto.getDisplayName())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .displayName(dto.getDisplayName())
                .build();
    }

    protected User mapUpdate(@NonNull UserUpdateDto dto, @NonNull User user) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDisplayName(dto.getDisplayName());
        return user;
    }

}
