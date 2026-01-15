package org.mifistudy.grechko.bookingservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RoomAvailabilityRequest {
    private UUID hotelId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer guestCount;
}
