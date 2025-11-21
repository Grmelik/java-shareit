package ru.practicum.shareit.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    User create(User user);

    Page<User> getAll(Pageable pageable);

    User getUserById(Long id);

    User update(Long id, User user);

    void delete(Long id);
}