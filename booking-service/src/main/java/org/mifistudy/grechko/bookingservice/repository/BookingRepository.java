package org.mifistudy.grechko.bookingservice.repository;

import org.mifistudy.grechko.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    // Бронирования пользователя
    //List<Booking> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Бронирования комнаты
   // List<Booking> findByRoomId(UUID roomId);

    // Активные бронирования комнаты на даты
   /* List<Booking> findByRoomIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            UUID roomId,
            List<Booking.Status> statuses,
            LocalDate startDate,
            LocalDate endDate
    );*/

    boolean existsByHotelIdAndRoomId(UUID hotelId, UUID roomId);

    @Modifying
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id = :id")
    void updateBookingStatus(@Param("id") UUID id, @Param("status") Booking.Status status);

    // Проверка пересечений дат
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b 
        WHERE b.roomId = :roomId 
        AND b.hotelId = :hotelId
        AND b.status IN :statuses
        AND b.startDate < :endDate 
        AND b.endDate > :startDate
    """)
    boolean existsOverlappingBooking(
            UUID hotelId,
            UUID roomId,
            List<Booking.Status> statuses,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Booking> findByUserId(UUID userId);
}