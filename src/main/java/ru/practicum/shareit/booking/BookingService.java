package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    public List<BookingDto> getUserBookings(BookingState state, Long userId) {
        getUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerId(userId, sort);
            case CURRENT -> bookingRepository.findByBookerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, now, now, sort);
            case PAST -> bookingRepository.findByBookerIdAndEndDateBefore(userId, now, sort);
            case FUTURE -> bookingRepository.findByBookerIdAndStartDateAfter(userId, now, sort);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
        };

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    public List<BookingDto> getOwnerBookings(BookingState state, Long ownerId) {
        getUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemOwnerId(ownerId, sort);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(ownerId,
                    now, now, sort);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndDateBefore(ownerId, now, sort);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartDateAfter(ownerId, now, sort);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sort);
        };

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не было найден"));

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toBookingDto(booking);
        }

        throw new AccessForbiddenException("У вас не прав на просмотр информации о бронировании");
    }

    public BookingDto createBooking(BookingCreateDto newBooking, Long userId) {
        User user = getUserById(userId);
        Item item = getItemById(newBooking.getItemId());

        if (newBooking.getStart().isAfter(newBooking.getEnd()) || newBooking.getStart().equals(newBooking.getEnd())) {
            throw new ValidationException("Время начала бронирования должно быть раньше окончания");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Владелец вещи не может забронировать свою же вещь");
        }

        if (!item.getIsAvailable()) {
            throw new BadRequestException("Товар не доступен к бронированию");
        }

        Booking booking = bookingMapper.toBookingFromCreateDto(newBooking, user, item);

        return bookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не было найден"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessForbiddenException("Вы не владелец вещи");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже было обработано");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);

        return bookingMapper.toBookingDto(booking);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не был найден"));
    }
}
