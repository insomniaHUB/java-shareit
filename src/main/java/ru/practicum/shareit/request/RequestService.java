package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RequestService {
    private static final Map<Long, ItemRequest> requestStorage = new HashMap<>();
    private static Long id = 0L;

}
