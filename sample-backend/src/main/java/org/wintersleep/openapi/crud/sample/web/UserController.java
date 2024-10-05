package org.wintersleep.openapi.crud.sample.web;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.UsersApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wintersleep.openapi.crud.core.provider.OffsetLimit;
import org.wintersleep.openapi.crud.core.provider.SortRequest;

import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class UserController implements UsersApi {

    private final UserDataProvider dataProvider;

    public UserController(UserDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public ResponseEntity<List<UserEntryDto>> listUsers(UserFilterDto filter, String search,
                                                        UserSortDto sort, SortOrder order,
                                                        Long start, Long end) {
        return dataProvider.list(filter, search, SortRequest.of(sort, order), OffsetLimit.ofStartEnd(start, end));
    }

    @Override
    public ResponseEntity<List<UserDto>> getManyUsers(List<Long> ids) {
        return dataProvider.getMany(ids);
    }

    @Override
    public ResponseEntity<UserDto> createUser(UserCreateDto dto) {
        return dataProvider.create(dto);
    }

    @Override
    public ResponseEntity<UserDto> readUser(Long id) {
        return dataProvider.read(id);
    }

    @Override
    public ResponseEntity<UserDto> updateUser(Long id, UserUpdateDto dto) {
        return dataProvider.update(id, dto);
    }

/*
    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
*/

}
