package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(User user) {
        validateCreatingUser(user);
        return userRepository.save(user);
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    @Override
    @Transactional
    public User update(Long id, User userNew) {
        User userOld = getUserById(id);
        validateUpdatingUser(userNew, userOld);
        return userRepository.save(userOld);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    private void validateCreatingUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Такой email уже зарегистрирован.");
        }
    }

    private void validateUpdatingUser(User userNew, User userOld) {
        if (userNew.getName() != null && !userNew.getName().isBlank()) {
            userOld.setName(userNew.getName());
        }

        if (userNew.getEmail() != null && userOld.getEmail() != null) {
            if (userRepository.findAll().stream()
                    .anyMatch(user -> userNew.getEmail().equals(userOld.getEmail()))) {
                throw new ConflictException("Такой email уже зарегистрирован.");
            }
            userOld.setEmail(userNew.getEmail());
        }
    }
}