package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Page<Item> getItemsByOwner(Long ownerId, Pageable pageable);

    Item getItemById(Long id);

    Item update(Long id, Item item);

    void delete(Long id);

    Page<Item> search(String text, Pageable pageable);

    Comment addComment(Long itemId, Comment comment, Long authorId);

    List<CommentDto> getCommentsByItemIdWithAuthor(Long itemId);

    Item getItemByIdWithDependencies(Long itemId);

    ItemBookingsDto getItemByIdFullData(Long id, Long userId);

    List<ItemBookingsDto> getItemsFullData(Long ownerId, Pageable pageable);
}