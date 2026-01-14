package org.mifistudy.grechko.hotelservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private UUID id;
    private UUID hotelId;
    private String hotelName;
    private String number;
    private Boolean available;
    private Integer timesBooked;

}
