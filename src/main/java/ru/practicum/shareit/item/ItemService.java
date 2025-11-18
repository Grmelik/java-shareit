package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    Page<Item> getItemsByOwner(Long ownerId, Pageable pageable);

    ItemBookingsDto getItemById(Long id, Long userId);

    ItemDto update(Long id, ItemDto itemDto, Long ownerId);

    void delete(Long id);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, Long authorId, CommentDto commentDto);

    ItemBookingsDto findAllCommentsByItemId(Long id, Long userId);
}