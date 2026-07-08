package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndEndDateBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartDateAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long bookerId, LocalDateTime nowStart,
                                                                                    LocalDateTime nowEnd, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long ownerId, LocalDateTime nowStart,
                                                                                       LocalDateTime nowEnd, Sort sort);

    List<Booking> findByItemOwnerIdAndEndDateBefore(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStartDateAfter(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    Booking findFirstByItemIdAndEndDateBeforeAndStatus(Long itemId, LocalDateTime now, BookingStatus status);

    Booking findFirstByItemIdAndStartDateAfterAndStatus(Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findByBookerIdAndItemIdAndStatusAndEndDateBefore(Long bookerId, Long itemId,
                                                                       BookingStatus status, LocalDateTime now);

    List<Booking> findByItemIdInAndStatus(List<Long> itemIds, BookingStatus status);
}
