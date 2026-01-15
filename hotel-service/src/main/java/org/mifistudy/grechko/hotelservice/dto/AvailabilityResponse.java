package org.mifistudy.grechko.hotelservice.dto;

import lombok.Builder;
import lombok.Data;
import org.mifistudy.grechko.hotelservice.entity.Room;

import java.util.UUID;

@Data
@Builder
public class AvailabilityResponse {
    private UUID id;
    private String number;
    private Integer timesBooked;// ID временной блокировки

    public AvailabilityResponse fromEntity(Room room) {
        return AvailabilityResponse.builder()
                .id(room.getId())
                .number(room.getNumber())
                .timesBooked(room.getTimesBooked())
                .build();
    }
}
