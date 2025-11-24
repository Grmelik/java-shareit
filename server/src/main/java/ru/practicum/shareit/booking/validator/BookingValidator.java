package ru.practicum.shareit.booking.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

@Component
@RequiredArgsConstructor
public class BookingValidator {
    private final ItemService itemService;
    private final UserService userService;

    public void validateCreatingBooking(Booking booking) {
        Item item = booking.getItem();
        userService.getUserById(booking.getBooker().getId());
        itemService.getItemById(item.getId());
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
    }

    public void validateApprovingBooking(Long ownerId, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Бронирование может подтвердить лишь владелец вещи");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование обработано");
        }
    }

    public void validateCancelingBooking(Long userId, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Бронирование может отменить лишь инициатор бронирования");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование можно отменять лишь в статусе WAITING");
        }
    }

    public void validateBookingAccess(Long userId, Booking booking) {
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new ForbiddenException("Доступ к бронированию запрещен");
        }
    }
}