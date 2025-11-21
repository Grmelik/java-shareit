package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.FacadeService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final FacadeService facadeService;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Item create(Item item) {
        userService.getUserById(item.getOwner().getId());
        validateCreatingItem(item);
        return itemRepository.save(item);
    }

    @Override
    public Page<Item> getItemsByOwner(Long ownerId, Pageable pageable) {
        userService.getUserById(ownerId);
        return itemRepository.findByOwnerId(ownerId, pageable);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена."));
    }

    @Override
    @Transactional
    public Item update(Long id, Item itemNew) {
        Item itemOld = getItemById(id);
        validateUpdatingItem(itemNew, itemOld);
        return itemRepository.save(itemOld);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getItemById(id);
        itemRepository.deleteById(id);
    }

    @Override
    public Page<Item> search(String text, Pageable pageable) {
        return itemRepository.search(text, pageable);
    }

    @Override
    @Transactional
    public Comment addComment(Long itemId, Comment comment, Long authorId) {
        Item item = getItemById(itemId);
        User author = userService.getUserById(authorId);
        validateCreatingComment(item, author);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public ItemBookingsDto createItemWithBooking(Item item, Long userId) {
        ItemBookingsDto itemDto = itemMapper.toBookingsDto(item);
        facadeService.addBookingInfo(userId, item, itemDto);
        List<CommentDto> comments = getCommentsByItemIdWithAuthor(item.getId());
        itemDto.setComments(comments);
        return itemDto;
    }

    @Override
    public Item getItemByIdWithDependencies(Long itemId) {
        return itemRepository.findItemWithDependencies(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    @Override
    public ItemBookingsDto getItemByIdFullData(Long id, Long userId) {
        Item item = getItemByIdWithDependencies(id);
        return createItemWithBooking(item, userId);
    }

    @Override
    public List<ItemBookingsDto> getItemsFullData(Long ownerId, Pageable pageable) {
        Page<Item> itemsPage = getItemsByOwner(ownerId, pageable);
        List<Long> itemIds = itemsPage.getContent().stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Item> itemWithDependencies = new ArrayList<>();
        if (!itemIds.isEmpty()) {
            itemWithDependencies = itemRepository.findItemsWithDependenciesByIds(itemIds);
        }
        return itemWithDependencies.stream()
                .map(item -> createItemWithBooking(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsByItemIdWithAuthor(Long itemId) {
        List<Comment> comments = commentRepository.findByItemIdWithAuthor(itemId);
        return commentMapper.toDtoList(comments);
    }

    private void validateCreatingItem(Item item) {
        if (item.getOwner() == null || item.getOwner().getId() == null) {
            throw new ValidationException("Владелец не найден");
        }
        userService.getUserById(item.getOwner().getId());
    }

    private void validateUpdatingItem(Item itemNew, Item itemOld) {
        if (!itemOld.getOwner().getId().equals(itemNew.getOwner().getId())) {
            throw new ForbiddenException("Обновлять вещь может лишь ее владелец");
        }
        if (itemNew.getName() != null && !itemNew.getName().isBlank()) {
            itemOld.setName(itemNew.getName());
        }
        if (itemNew.getDescription() != null && !itemNew.getDescription().isBlank()) {
            itemOld.setDescription(itemNew.getDescription());
        }
        if (itemNew.getAvailable() != null) {
            itemOld.setAvailable(itemNew.getAvailable());
        }
    }

    private void validateCreatingComment(Item item, User author) {
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatus(author.getId(),
                item.getId(), BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не бронировал эту вещь");
        }

        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        boolean hasFinishedBookings = bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(author.getId(), item.getId(),
                        BookingStatus.APPROVED, currentTime);
        if (!hasFinishedBookings) {
            throw new ValidationException("Бронирование еще не завершено");
        }
    }
}