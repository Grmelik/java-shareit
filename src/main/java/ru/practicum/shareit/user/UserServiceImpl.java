package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private static Long newId = 1L;

    @Override
    public UserDto create(UserDto userDto) {
        validateUser(userDto);
        User user = UserMapper.toUser(userDto);
        user.setId(generateId());
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        if (users.isEmpty()) {
            throw new NotFoundException("Список пользователей пуст.");
        }

        return users.values()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User userOld = users.get(id);
        validateOldUser(userOld, userDto);
        userOld.setName(userDto.getName());
        userOld.setEmail(userDto.getEmail());

        return UserMapper.toUserDto(userOld);
    }

    @Override
    public void delete(Long id) {
        if (id != null) {
            users.remove(id);
        }
    }

    private void validateUser(UserDto userDto) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail())))
            throw new ValidationException("Такой email уже зарегистрирован.");
    }

    private void validateOldUser(User userOld, UserDto userDto) {
        if (userOld == null)
            throw new NotFoundException("Пользователь не найден!");
        if (userOld.getEmail() != null && userDto.getEmail() != null) {
            if (users.values().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail())))
                throw new ValidationException("Такой email уже зарегистрирован.");
        }
    }

    private long generateId() {
        return newId++;
    }
}