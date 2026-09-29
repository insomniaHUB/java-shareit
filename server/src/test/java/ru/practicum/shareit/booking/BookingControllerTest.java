package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto request = BookingCreateDto.builder().start(now.plusDays(1)).end(now.plusDays(2)).build();
        BookingDto response = BookingDto.builder().id(1L).start(now.plusDays(1)).end(now.plusDays(2)).status(BookingStatus.WAITING).build();
        when(bookingService.createBooking(any(BookingCreateDto.class), eq(1L))).thenReturn(response);

        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingDto response = BookingDto.builder().id(1L).start(now.plusDays(1)).end(now.plusDays(2)).status(BookingStatus.APPROVED).build();
        when(bookingService.approveBooking(eq(1L), eq(1L), eq(true))).thenReturn(response);

        mvc.perform(patch("/bookings/1")
                .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getUserBookings() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        when(bookingService.getUserBookings(BookingState.ALL, 1L)).thenReturn(List.of(
                BookingDto.builder().id(1L).start(now.plusDays(1)).end(now.plusDays(2)).status(BookingStatus.APPROVED).build()
        ));

        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getOwnerBookings() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        when(bookingService.getOwnerBookings(BookingState.ALL, 1L)).thenReturn(List.of(
                BookingDto.builder().id(1L).start(now.plusDays(1)).end(now.plusDays(2)).status(BookingStatus.APPROVED).build()
        ));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getBookingById() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingDto response = BookingDto.builder().id(1L).start(now.plusDays(1)).end(now.plusDays(2)).status(BookingStatus.APPROVED).build();
        when(bookingService.getBookingById(1L, 1L)).thenReturn(response);

        mvc.perform(get("/bookings/1")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
