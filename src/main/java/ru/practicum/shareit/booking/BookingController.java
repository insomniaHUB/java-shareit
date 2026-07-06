package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(defaultValue = "ALL") BookingState state,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getUserBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(defaultValue = "ALL") BookingState state,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.getOwnerBookings(state, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingCreateDto booking,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }
}
