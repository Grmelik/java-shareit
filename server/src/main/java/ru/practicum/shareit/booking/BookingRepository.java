package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            SELECT b FROM Booking b
             WHERE b.item.id = :itemId
               AND b.status = :status
               AND b.end < :currentTime
             ORDER BY b.end DESC
            """)
    Optional<Booking> findLastBooking(Long itemId,
                                      LocalDateTime currentTime,
                                      BookingStatus status);

    @Query("""
            SELECT b FROM Booking b
             WHERE b.item.id = :itemId
               AND b.start > :currentTime
               AND b.status = :status
             ORDER BY b.start ASC
            """)
    Optional<Booking> findNextBooking(Long itemId,
                                      LocalDateTime currentTime,
                                      BookingStatus status);

    @Query("""
            SELECT b FROM Booking b
             WHERE b.booker.id = :bookerId
               AND b.item.id = :itemId
               AND b.status = :status
            """)
    List<Booking> findByBookerIdAndItemIdAndStatus(Long bookerId,
                                                   Long itemId,
                                                   BookingStatus status);

    @Query("""
            SELECT b FROM Booking b
             WHERE b.booker.id = :bookerId
               AND b.item.id = :itemId
               AND b.status = :status
               AND b.end < :currentTime
            """)
    List<Booking> findFinishedBookingsByUserAndItem(Long bookerId,
                                                    Long itemId,
                                                    BookingStatus status,
                                                    LocalDateTime currentTime);

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId, Long itemId, BookingStatus status,
                                                           LocalDateTime end);
}