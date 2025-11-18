package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

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

    private ItemDto item;

    private UserDto booker;

    private String status;
}