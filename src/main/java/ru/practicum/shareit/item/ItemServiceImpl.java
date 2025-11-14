package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        if (userService.getUserById(ownerId) == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public Page<Item> getItemsByOwner(Long ownerId, Pageable pageable) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        return itemRepository.findByOwnerId(owner.getId(), pageable);
    }

    @Override
    public ItemBookingsDto getItemById(Long id, Long userId) {
        return findAllCommentsByItemId(id, userId);
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        Item itemOld = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена."));
        validateOldItem(itemOld, itemDto, ownerId);
        if (!itemOld.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Обновлять вещь может лишь ее владелец");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemOld.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            itemOld.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemOld.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(itemOld));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id != null) {
            itemRepository.deleteById(id);
        }
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long authorId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь"));
        boolean hasCompletedBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                authorId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new ValidationException("Бронирование еще не завершено");
        }
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public ItemBookingsDto findAllCommentsByItemId(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена."));
        ItemBookingsDto itemDto = ItemMapper.toItemBookingsDto(item);
        bookingService.addBookingInfo(itemDto, item, userId);
        List<CommentDto> comments = commentRepository.findAllByItem(item).stream()
                .map(CommentMapper::toCommentDto)
                .toList();
        itemDto.setComments(comments);
        return itemDto;
    }

    private void validateOldItem(Item itemOld, ItemDto itemDto, Long ownerId) {
        if (itemOld == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        if (!itemOld.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Вещь может редактировать только собственник");
        }
    }
}