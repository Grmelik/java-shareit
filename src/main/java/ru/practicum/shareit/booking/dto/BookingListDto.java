package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingListDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private UserDto booker;
    private ItemDto item;
}
