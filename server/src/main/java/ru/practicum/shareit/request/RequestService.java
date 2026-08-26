package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<ItemInRequestDto> items = getRequestsItems(itemRequest.getId());

        return requestMapper.toRequestDto(itemRequest, items);
    }

    public List<ItemRequestDto> getUsersItemRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<ItemInRequestDto>> itemsByRequestId = itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toItemInRequestDto)
                .collect(Collectors.groupingBy(ItemInRequestDto::getRequestId));

        return requests.stream()
                .map(request -> {
                    List<ItemInRequestDto> requestItems = itemsByRequestId.getOrDefault(request.getId(), List.of());
                    return requestMapper.toRequestDto(request, requestItems);
                })
                .toList();
    }

    public List<ItemRequestDto> getRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNot(userId);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<ItemInRequestDto>> itemsByRequestId = itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toItemInRequestDto)
                .collect(Collectors.groupingBy(ItemInRequestDto::getRequestId));

        return requests.stream()
                .map(request -> {
                    List<ItemInRequestDto> requestItems = itemsByRequestId.getOrDefault(request.getId(), List.of());
                    return requestMapper.toRequestDto(request, requestItems);
                })
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }

    public ItemRequestDto getRequest(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        List<ItemInRequestDto> items = getRequestsItems(requestId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не был найден"));

        return requestMapper.toRequestDto(request, items);
    }

    private List<ItemInRequestDto> getRequestsItems(Long requestId) {
        return itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemInRequestDto)
                .toList();
    }
}
