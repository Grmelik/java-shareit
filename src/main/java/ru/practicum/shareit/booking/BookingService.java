package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.Item;

public interface BookingService {
    BookingResponseDto create(Long userId, BookingDto bookingDto);

    BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    Page<Booking> getAllBookingsByUser(Long userId, Pageable pageable);

    Page<Booking> getAllBookingsByOwner(Long ownerId, Pageable pageable);

    void addBookingInfo(ItemBookingsDto dto, Item item, Long userId);
}
