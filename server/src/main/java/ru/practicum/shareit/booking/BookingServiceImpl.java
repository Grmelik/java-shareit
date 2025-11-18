package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingValidator bookingValidator;
    private final UserService userService;

    @Override
    @Transactional
    public Booking create(Booking booking) {
        bookingValidator.validateCreatingBooking(booking);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking approve(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = getBookingById(bookingId);
        bookingValidator.validateApprovingBooking(ownerId, booking);

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    @Override
    public Page<Booking> getAllBookingsByUser(Long userId, Pageable pageable) {
        userService.getUserById(userId);
        return bookingRepository.findByBookerId(userId, pageable);
    }

    @Override
    public Page<Booking> getAllBookingsByOwner(Long ownerId, Pageable pageable) {
        userService.getUserById(ownerId);
        return bookingRepository.findByItemOwnerId(ownerId, pageable);
    }

    @Override
    @Transactional
    public void cancelBooking(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        bookingValidator.validateCancelingBooking(userId, booking);
    }

    @Override
    public Booking getBookingByIdWithAccessCheck(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);
        bookingValidator.validateBookingAccess(userId, booking);
        return booking;
    }
}
