package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;

    @PostMapping
    public ItemRequestsDto createItemRequest(
            @RequestBody ItemRequestRequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") long requesterId) {

        return itemRequestMapper.toDto(itemRequestService.createRequest(
                itemRequestMapper.toEntity(requestDto,
                        userService.getUserById(requesterId)))
        );
    }

    @GetMapping("/{requestId}")
    public ItemRequestsDto getRequestById(@PathVariable long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getItemRequestDtoById(requestId);
    }

    @GetMapping
    public List<ItemRequestsDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestService.getUserRequestsDtoList(userId, pageable);
    }

    @GetMapping("/all")
    public List<ItemRequestsDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestService.getAllRequestsDtoList(pageable);
    }

    @GetMapping("/other")
    public List<ItemRequestsDto> getOtherUserRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestService.getOtherUserRequestsDtoList(userId, pageable);
    }
}
