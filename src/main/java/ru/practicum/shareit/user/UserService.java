package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    Collection<UserDto> getAll();

    UserDto getUserById(Long id);

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);
}