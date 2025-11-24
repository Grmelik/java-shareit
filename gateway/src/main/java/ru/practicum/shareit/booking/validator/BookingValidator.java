package ru.practicum.shareit.booking.validator;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;

@Component
public class BookingValidator {

    public void validateBookingCreation(BookItemRequestDto bookingDto) {
        if (bookingDto.getItemId() == null) {
            throw new ValidationException("ID предмета обязателен");
        }
        if (bookingDto.getStart() == null) {
            throw new ValidationException("Дата начала бронирования обязательна");
        }
        if (bookingDto.getEnd() == null) {
            throw new ValidationException("Дата окончания бронирования обязательна");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования должна быть ранее даты окочнания");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания бронирования должна быть позже даты окончания");
        }
    }

    public void validateBookingState(String state) {
        if (state == null) {
            return;
        }
        try {
            BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Некорректный статус бронирования: " + state);
        }
    }

    public void validateBookingApproval(Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Параметр approved обязателен");
        }
    }

    public void validateBookingId(Long bookingId) {
        if (bookingId == null || bookingId <= 0) {
            throw new ValidationException("ID бронирования должен быть положительным числом");
        }
    }
}