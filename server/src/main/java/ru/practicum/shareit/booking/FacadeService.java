package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacadeService {
    private final BookingRepository bookingRepository;

    @Transactional
    public void addBookingInfo(Long userId, Item item, ItemBookingsDto dto) {
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            getLastBooking(item.getId()).ifPresent(dto::setLastBooking);
            getNextBooking(item.getId()).ifPresent(dto::setNextBooking);
        }
    }

    private Optional<BookingDateDto> getLastBooking(Long itemId) {
        return bookingRepository.findLastBooking(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .map(this::mappingToBookingDateDto);
    }

    private Optional<BookingDateDto> getNextBooking(Long itemId) {
        return bookingRepository.findNextBooking(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .map(this::mappingToBookingDateDto);
    }

    private BookingDateDto mappingToBookingDateDto(Booking booking) {
        return new BookingDateDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}

