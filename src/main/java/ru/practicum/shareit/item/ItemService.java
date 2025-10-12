package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    Collection<ItemDto> getItemByOwner(Long ownerId);

    ItemDto getItemById(Long id);

    ItemDto update(Long id, ItemDto itemDto, Long ownerId);

    void delete(Long id);

    List<ItemDto> searchAvailableItems(String text);
}