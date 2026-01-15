package org.mifistudy.grechko.bookingservice.dto;

import lombok.Builder;
import lombok.Data;
import org.mifistudy.grechko.bookingservice.entity.Booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class BookingResponse {
    private UUID id;
    private UUID userId;
    private UUID roomId;
    private UUID hotelId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Booking.Status status;
}
