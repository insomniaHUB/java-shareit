package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingChangeDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(userMapper.toUserDto(booking.getBooker()))
                .item(itemMapper.toItemDto(booking.getItem()))
                .build();
    }

    public Booking toBookingFromCreateDto(BookingCreateDto dto, User user, Item item) {
        return Booking.builder()
                .startDate(dto.getStart())
                .endDate(dto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    public Booking toBookingFromChangeDto(BookingChangeDto dto, User user, Item item, BookingStatus status) {
        return Booking.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .item(item)
                .booker(user)
                .status(status)
                .build();
    }
}
