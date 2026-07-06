package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper mapper;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public ItemGetDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет не был найден"));
        List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        return mapper.toItemGetDto(item, comments);
    }

    public List<ItemBookingsDto> getItems(Long userId) {
        userService.getUserById(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                            .map(commentMapper::toCommentDto)
                            .toList();
                    BookingDto lastBooking = getLastBooking(item.getId());
                    BookingDto nextBooking = getNextBooking(item.getId());
                    return mapper.toItemBookingDto(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItemByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.findByText(text).stream()
                .map(mapper::toItemDto)
                .toList();
    }

    public ItemDto createItem(ItemCreateDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        Item item = mapper.toItemFromCreateDto(itemDto, owner);

        return mapper.toItemDto(itemRepository.save(item));
    }

    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не был найден"));
        Comment comment = commentMapper.toComment(commentDto, author, item);

        if (bookingRepository.findByBookerIdAndItemIdAndStatusAndEndDateBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("У вас нет прав комментировать вещь при незавершенном бронировании");
        }

        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    public ItemDto changeItem(Long itemId, ItemChangeDto itemChangeDto, Long userId) {
        userService.getUserById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не был найден"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessForbiddenException("Доступ запрещен");
        }

        if (itemChangeDto.getDescription() != null && !itemChangeDto.getDescription().isBlank()) {
            item.setDescription(itemChangeDto.getDescription());
        }
        if (itemChangeDto.getName() != null && !itemChangeDto.getName().isBlank()) {
            item.setName(itemChangeDto.getName());
        }
        if (itemChangeDto.getIsAvailable() != null) {
            item.setIsAvailable(itemChangeDto.getIsAvailable());
        }

        return mapper.toItemDto(itemRepository.save(item));
    }

    private BookingDto getLastBooking(Long itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndStartDateBefore(itemId, LocalDateTime.now());

        if (booking == null) {
            return null;
        }

        return bookingMapper.toBookingDto(booking);
    }

    private BookingDto getNextBooking(Long itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndStartDateAfter(itemId, LocalDateTime.now());

        if (booking == null) {
            return null;
        }

        return bookingMapper.toBookingDto(booking);
    }
}
