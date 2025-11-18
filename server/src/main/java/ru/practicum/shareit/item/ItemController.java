package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemMapper.toDto(itemService.create(itemMapper.toItem(itemRequestDto,
                userService.getUserById(ownerId),
                itemRequestDto.getRequestId() != null ?
                        itemRequestService.getRequestById(itemRequestDto.getRequestId()) : null)));
    }

    @GetMapping
    public List<ItemBookingsDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemService.getItemsFullData(ownerId, pageable);
    }

    @GetMapping("/{id}")
    public ItemBookingsDto getItemById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemByIdFullData(id, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id,
                          @RequestBody ItemRequestDto itemRequestDto,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemMapper.toDto(itemService.update(id, itemMapper.toItem(itemRequestDto,
                userService.getUserById(ownerId),
                itemRequestDto.getRequestId() != null ?
                        itemRequestService.getRequestById(itemRequestDto.getRequestId()) : null)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") Integer from,
                                @RequestParam(defaultValue = "10") Integer size,
                                @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemService.search(text, pageable).getContent().stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(@PathVariable Long id,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return commentMapper.toDto(
                itemService.addComment(id,
                        commentMapper.toEntity(commentDto,
                                itemService.getItemByIdWithDependencies(id),
                                userService.getUserById(authorId)),
                        authorId)
        );
    }
}