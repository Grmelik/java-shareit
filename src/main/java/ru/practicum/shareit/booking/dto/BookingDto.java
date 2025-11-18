package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private Long id;

    @NotNull(message = "Укажите дату начала бронирования")
    private LocalDateTime start;

    @NotNull(message = "Укажите дату окончания бронирования")
    private LocalDateTime end;

    @NotNull(message = "Укажите идентификатор вещи")
    private Long itemId;

    private Long bookerId;

    private BookingStatus status;
}
