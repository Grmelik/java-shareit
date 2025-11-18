package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestsDto {
    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;
    private UserDto requester;
    private List<ItemDto> items = new ArrayList<>();
}