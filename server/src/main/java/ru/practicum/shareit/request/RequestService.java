package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper requestMapper;

    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        ItemRequest itemRequest = itemRequestRepository.save(requestMapper.toItemRequest(itemRequestDto, owner));
        List<ItemDto> items = getRequestsItems(itemRequest.getId());

        return requestMapper.toRequestDto(itemRequest, items);
    }

    public List<ItemRequestDto> getUsersItemRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        List<ItemRequestDto> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(request -> requestMapper.toRequestDto(request, getRequestsItems(request.getId())))
                .toList();

        return requests;
    }

    public List<ItemRequestDto> getRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        List<ItemRequestDto> requests = itemRequestRepository.findByRequestorIdNot(userId).stream()
                .map(request -> requestMapper.toRequestDto(request, getRequestsItems(request.getId())))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();

        return requests;
    }

    public ItemRequestDto getRequest(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        List<ItemDto> items = getRequestsItems(requestId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не был найден"));

        return requestMapper.toRequestDto(request, items);
    }

    private List<ItemDto> getRequestsItems(Long requestId) {
        return itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
