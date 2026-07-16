package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingChangeDto {
    @FutureOrPresent
    private LocalDateTime startDate;
    @Future
    private LocalDateTime endDate;
    private Long itemId;
}
