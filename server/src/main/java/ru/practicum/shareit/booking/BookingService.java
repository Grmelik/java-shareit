package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {
    Booking create(Booking booking);

    Booking approve(Long ownerId, Long bookingId, Boolean approved);

    Booking getBookingById(Long bookingId);

    Page<Booking> getAllBookingsByUser(Long userId, Pageable pageable);

    Page<Booking> getAllBookingsByOwner(Long ownerId, Pageable pageable);

    Booking getBookingByIdWithAccessCheck(Long bookingId, Long userId);

    void cancelBooking(Long userId, Long bookingId);
}
