package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestParam(defaultValue = "ALL") BookingState state,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingClient.getBookings(state, userId, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestParam(defaultValue = "ALL") BookingState state,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingClient.getOwnerBookings(state, ownerId);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto booking,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Время начала бронирования должно быть раньше окончания");
        }

        return bookingClient.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }
}
