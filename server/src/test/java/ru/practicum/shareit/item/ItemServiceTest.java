package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Spy
    private ItemMapper mapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getItemById_success() {
        Item item = Item.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        User author = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        Comment comment = Comment.builder().id(1L).author(author).text("Awesome chainsaw").item(item).created(LocalDateTime.now()).build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(List.of(comment));

        ItemGetDto result = itemService.getItemById(1L, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getDescription()).isEqualTo("Very cool chainsaw");
        assertThat(result.getName()).isEqualTo("Chainsaw");
        assertThat(result.getIsAvailable()).isTrue();
    }

    @Test
    void createItem_success() {
        User user = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        ItemDto dto = ItemDto.builder().name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(Item.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build());

        ItemDto result = itemService.createItem(dto, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Chainsaw");
        assertThat(result.getIsAvailable()).isTrue();
    }

    @Test
    void getItemByText_success() {
        Item item1 = Item.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        Item item2 = Item.builder().id(1L).name("Chainsaw2").description("Very cool chainsaw2").isAvailable(true).build();
        when(itemRepository.findByText("Chainsaw")).thenReturn(List.of(item1, item2));

        List<ItemDto> result = itemService.getItemByText("Chainsaw");

        assertThat(result).hasSize(2);
    }

    @Test
    void getItems_success() {
        LocalDateTime now = LocalDateTime.now();

        User owner = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        Item item = Item.builder().id(1L).name("Axe").description("Big axe").isAvailable(false).owner(owner).build();

        Booking lastBooking = Booking.builder()
                .id(1L).item(item).status(BookingStatus.APPROVED)
                .startDate(now.minusDays(2)).endDate(now.minusDays(1))
                .build();

        Booking nextBooking = Booking.builder()
                .id(2L).item(item).status(BookingStatus.APPROVED)
                .startDate(now.plusDays(1)).endDate(now.plusDays(2))
                .build();

        Comment comment = Comment.builder().id(1L).text("Great tool").item(item).author(owner).build();
        BookingDto lastBookingDto = BookingDto.builder().id(1L).build();
        BookingDto nextBookingDto = BookingDto.builder().id(2L).build();
        CommentDto commentDto = CommentDto.builder().id(1L).text("Great tool").build();
        when(userService.getUserById(1L)).thenReturn(null);
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findByItemIdIn(List.of(1L))).thenReturn(List.of(comment));
        when(bookingRepository.findByItemIdInAndStatus(List.of(1L), BookingStatus.APPROVED))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
        when(bookingMapper.toBookingDto(lastBooking)).thenReturn(lastBookingDto);
        when(bookingMapper.toBookingDto(nextBooking)).thenReturn(nextBookingDto);

        List<ItemBookingsDto> result = itemService.getItems(1L);
        ItemBookingsDto itemResult = result.getFirst();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(itemResult.getId()).isEqualTo(1L);
        assertThat(itemResult.getName()).isEqualTo("Axe");
        assertThat(itemResult.getDescription()).isEqualTo("Big axe");
        assertThat(itemResult.getIsAvailable()).isFalse();
        assertThat(itemResult.getLastBooking()).isEqualTo(lastBookingDto);
        assertThat(itemResult.getNextBooking()).isEqualTo(nextBookingDto);
        assertThat(itemResult.getComments()).containsExactly(commentDto);
        verify(userService, times(1)).getUserById(1L);
        verify(itemRepository, times(1)).findByOwnerId(1L);
        verify(commentRepository, times(1)).findByItemIdIn(anyList());
        verify(bookingRepository, times(1)).findByItemIdInAndStatus(anyList(), any());
    }

    @Test
    void createComment_success() {
        User author = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        Item item = Item.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        ItemDto itemDto = ItemDto.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        Comment comment = Comment.builder().id(1L).author(author).text("Awesome chainsaw").item(item).created(LocalDateTime.now()).build();
        CommentDto commentDto = CommentDto.builder().id(1L).authorName("Kirill").text("Awesome chainsaw").item(itemDto).created(LocalDateTime.now()).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndDateBefore(eq(1L), eq(1L),
                eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(Optional.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toComment(any(CommentDto.class), any(User.class), any(Item.class), any(LocalDateTime.class)))
                .thenReturn(comment);
        when(commentMapper.toCommentDto(comment))
                .thenReturn(commentDto);

        CommentDto result = itemService.createComment(1L, 1L, commentDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Awesome chainsaw");
        assertThat(result.getAuthorName()).isEqualTo("Kirill");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void changeItem_success() {
        User user = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        Item item = Item.builder().id(1L).name("Axe").description("Big axe").isAvailable(false).owner(user).build();
        Item item2 = Item.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).owner(user).build();
        ItemDto dto = ItemDto.builder().name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        when(userService.getUserById(1L)).thenReturn(null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item2);

        ItemDto result = itemService.changeItem(1L, dto, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Very cool chainsaw");
        assertThat(result.getIsAvailable()).isTrue();
        verify(itemRepository, times(1)).save(any(Item.class));
    }
}
