package org.wintersleep.crud.web;

import lombok.NonNull;
import org.openapitools.api.UsersApi;
import org.openapitools.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wintersleep.crud.domain.User;
import org.wintersleep.crud.domain.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/")
public class UserController
        extends AbstractCrudController<User, Long, UserEntryDto, UserFilterDto, UserCreateDto, UserDto, UserUpdateDto>
        implements UsersApi {

    public UserController(UserRepository userRepository) {
        super("users", userRepository);
    }

    @Override
    public ResponseEntity<UserDto> createUser(UserCreateDto dto) {
        return create(dto);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<UserEntryDto>> listUser(Integer page, Integer size, UserFilterDto filter, String sort, Pageable pageable) {
        return list(filter, pageable);
    }

    @Override
    public ResponseEntity<UserDto> readUser(Long id) {
        return read(id);
    }

    @Override
    public ResponseEntity<UserDto> updateUser(Long id, UserUpdateDto dto) {
        return update(id, dto);
    }

    protected UserEntryDto mapEntry(@NonNull User user) {
        return UserEntryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .displayName(user.getDisplayName())
                .build();
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
