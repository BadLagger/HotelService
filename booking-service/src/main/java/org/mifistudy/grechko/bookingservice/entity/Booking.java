package org.mifistudy.grechko.bookingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @Column(name = "hotel_id", nullable = false)
    private UUID hotelId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.COMPLETED;

   // @Column(name = "created_at", nullable = false)
   // @Builder.Default
   // private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        PENDING,     // Ожидает подтверждения
        CONFIRMED,   // Подтверждено
        CANCELLED,   // Отменено
        COMPLETED    // Завершено (после выезда)
    }
}
