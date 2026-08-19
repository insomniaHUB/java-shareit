package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_success() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder().start(start).end(end).itemId(1L).build();
        User user = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        User user2 = User.builder().id(2L).name("Kirill2").email("kirill2@gmail.com").build();
        User booker = User.builder().name("Anton").email("antonio@gmail.com").build();
        Item item = Item.builder().id(1L).name("Chainsaw").owner(user2).description("Very cool chainsaw").isAvailable(true).build();
        Booking booking = Booking.builder().id(1L).booker(booker).item(item).status(BookingStatus.APPROVED).startDate(start).endDate(end).build();
        BookingDto bookingDto = BookingDto.builder().id(1L).status(BookingStatus.APPROVED).start(start).end(end).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.toBookingFromCreateDto(bookingCreateDto, user, item)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(bookingCreateDto, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveBooking_success() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder().id(2L).owner(owner).build();
        Booking booking = Booking.builder().id(1L).item(item).status(BookingStatus.WAITING).build();
        BookingDto expectedDto = BookingDto.builder().id(1L).status(BookingStatus.APPROVED).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(expectedDto);

        BookingDto result = bookingService.approveBooking(1L, 1L, true);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void getBookingById_success() {
        User booker = User.builder().id(1L).name("Booker").build();
        User owner = User.builder().id(20L).name("Owner").build();
        Item item = Item.builder().id(5L).owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(booker).item(item).build();
        BookingDto expectedDto = BookingDto.builder().id(1L).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(expectedDto);

        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookingMapper, times(1)).toBookingDto(booking);
    }

    @Test
    void getOwnerBookings_success() {
        User owner = User.builder().id(1L).build();
        Booking booking = Booking.builder().id(10L).build();
        BookingDto bookingDto = BookingDto.builder().id(10L).build();
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerId(1L, sort)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getOwnerBookings(BookingState.ALL, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(10L);
        verify(bookingRepository, times(1)).findByItemOwnerId(1L, sort);
    }

    @Test
    void getUserBookings_success() {
        User user = User.builder().id(1L).build();
        Booking booking = Booking.builder().id(100L).build();
        BookingDto bookingDto = BookingDto.builder().id(100L).build();
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(1L, sort)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getUserBookings(BookingState.ALL, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(100L);
        verify(bookingRepository, times(1)).findByBookerId(1L, sort);
    }
}
