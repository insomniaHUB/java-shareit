package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingService {
    private static final Map<Long, Booking> bookingStorage = new HashMap<>();
    private static Long id = 0L;

}
