package org.wintersleep.openapi.crud.sample.web;

import lombok.extern.slf4j.Slf4j;
import org.wintersleep.openapi.crud.sample.api.UsersApi;
import org.wintersleep.openapi.crud.sample.api.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<UserDto>> listUsers(List<Long> ids,
                                                   UserFilterDto filter,
                                                   String search,
                                                   UserSortOrderDto sortOrder,
                                                   SampleStartEndDto startEnd) {
        return dataProvider.list(ids, filter, search, sortOrder, startEnd);
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
