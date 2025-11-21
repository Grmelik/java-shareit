package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.validator.ItemValidator;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final ItemValidator validator;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
        validator.validateItemCreation(itemRequestDto);
        return itemClient.createItem(ownerId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
        validator.validateItemId(itemId);
        return itemClient.updateItem(ownerId, itemId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemWithBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable Long itemId) {
        validator.validateItemId(itemId);
        return itemClient.getItemWithBookings(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemsWithBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemClient.getUserItemsWithBookings(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size,
                                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        validator.validateSearchText(text);
        return itemClient.searchItems(text, from, size, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId) {
        validator.validateItemId(itemId);
        return itemClient.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") long authorId,
                                                @RequestBody @Valid CommentDto commentDto) {
        validator.validateItemId(itemId);
        validator.validateComment(commentDto);
        return itemClient.createComment(authorId, itemId, commentDto);
    }
}