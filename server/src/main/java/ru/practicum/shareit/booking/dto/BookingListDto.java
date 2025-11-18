package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingListDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private Long bookerId;
    private String bookerName;
    private Long itemId;
    private String itemName;
}