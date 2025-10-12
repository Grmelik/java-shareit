package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items = new HashMap<>();
    private static Long newId = 1L;
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        if (userService.getUserById(ownerId) == null)
            throw new NotFoundException("Пользователь не найден.");
        Item item = ItemMapper.toItem(itemDto);
        item.setId(generateId());
        item.setOwnerId(ownerId);
        items.put(item.getId(), item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getItemByOwner(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> Objects.equals(item.getOwnerId(), ownerId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = items.get(id);
        if (item == null)
            throw new NotFoundException("Вещь не найдена.");

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        Item itemOld = items.get(id);
        validateOldItem(itemOld, itemDto, ownerId);
        itemOld.setName(itemDto.getName());
        itemOld.setDescription(itemDto.getDescription());
        itemOld.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(itemOld);
    }

    @Override
    public void delete(Long id) {
        if (id != null)
            items.remove(id);
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        List<Item> items = findAvailableItemsByText(text);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private List<Item> findAvailableItemsByText(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerCaseText = searchText.toLowerCase();

        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> containsText(item, lowerCaseText))
                .collect(Collectors.toList());
    }

    private boolean containsText(Item item, String searchText) {
        return (item.getName() != null && item.getName().toLowerCase().contains(searchText)) ||
                (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchText));
    }

    private void validateOldItem(Item itemOld, ItemDto itemDto, Long ownerId) {
        if (itemOld == null)
            throw new NotFoundException("Вещь не найдена");
        if (!Objects.equals(itemOld.getOwnerId(), ownerId))
            throw new ForbiddenException("Вещь может редактировать только собственник");
    }

    private long generateId() {
        return newId++;
    }
}