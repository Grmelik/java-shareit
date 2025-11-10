package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        validateUser(userDto);
        User user = UserMapper.toUser(userDto);
        User userSaved = userRepository.save(user);
        return UserMapper.toUserDto(userSaved);
    }

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User userOld = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            userOld.setName(userDto.getName());
        }

        if (userOld.getEmail() != null && userDto.getEmail() != null) {
            if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
                throw new ValidationException("Такой email уже зарегистрирован.");
            }
            userOld.setEmail(userDto.getEmail());
        }

        return UserMapper.toUserDto(userOld);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    private void validateUser(UserDto userDto) {
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new ValidationException("Такой email уже зарегистрирован.");
        }
    }
}