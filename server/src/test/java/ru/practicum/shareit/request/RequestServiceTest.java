package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper requestMapper;
    @InjectMocks
    private RequestService requestService;

    @Test
    void createItemRequest_success() {
        User user = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        ItemRequest request = ItemRequest.builder().id(1L).requestor(user).description("I wanna that chainsaw").build();
        ItemRequestDto requestDto = ItemRequestDto.builder().id(1L).userId(1L).description("I wanna that chainsaw").build();
        Item item = Item.builder().id(1L).owner(user).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestMapper.toItemRequest(requestDto, user)).thenReturn(request);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        when(requestMapper.toRequestDto(any(ItemRequest.class), any(List.class))).thenReturn(requestDto);

        ItemRequestDto result = requestService.createItemRequest(requestDto, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("I wanna that chainsaw");
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getUsersItemRequests_success() {
        User user = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        ItemRequest request = ItemRequest.builder().id(1L).requestor(user).description("I wanna that chainsaw").build();
        Item item = Item.builder().id(1L).owner(user).name("Chainsaw").description("Very cool chainsaw").request(request).isAvailable(true).build();
        ItemRequestDto requestDto = ItemRequestDto.builder().id(1L).userId(1L).description("I wanna that chainsaw").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(1L))).thenReturn(List.of(item));
        when(requestMapper.toRequestDto(any(ItemRequest.class), anyList())).thenReturn(requestDto);

        List<ItemRequestDto> result = requestService.getUsersItemRequests(1L);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getDescription()).isEqualTo("I wanna that chainsaw");
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(1L);
    }

    @Test
    void getRequests_success() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();

        ItemRequest earlyRequest = ItemRequest.builder().id(10L).description("Older request").build();
        ItemRequest lateRequest = ItemRequest.builder().id(20L).description("Newer request").build();
        ItemRequestDto earlyRequestDto = ItemRequestDto.builder().id(10L).description("Older request").created(now.minusDays(1)).build();
        ItemRequestDto lateRequestDto = ItemRequestDto.builder().id(20L).description("Newer request").created(now).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdNot(1L)).thenReturn(List.of(earlyRequest, lateRequest));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(Collections.emptyList());
        when(requestMapper.toRequestDto(eq(earlyRequest), any(List.class))).thenReturn(earlyRequestDto);
        when(requestMapper.toRequestDto(eq(lateRequest), any(List.class))).thenReturn(lateRequestDto);

        List<ItemRequestDto> result = requestService.getRequests(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(20L);
        assertThat(result.get(0).getDescription()).isEqualTo("Newer request");
        assertThat(result.get(1).getId()).isEqualTo(10L);
        assertThat(result.get(1).getDescription()).isEqualTo("Older request");
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findByRequestorIdNot(1L);
    }

    @Test
    void getRequest_success() {
        User user = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        ItemRequest request = ItemRequest.builder().id(1L).requestor(user).description("I wanna that chainsaw").build();
        Item item = Item.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").owner(user).request(request).isAvailable(true).build();
        ItemRequestDto requestDto = ItemRequestDto.builder().id(1L).userId(1L).description("I wanna that chainsaw").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestMapper.toRequestDto(any(ItemRequest.class), any(List.class))).thenReturn(requestDto);

        ItemRequestDto result = requestService.getRequest(1L, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("I wanna that chainsaw");
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findByRequestId(1L);
        verify(itemRequestRepository, times(1)).findById(1L);
    }
}
