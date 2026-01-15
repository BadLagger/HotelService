package org.mifistudy.grechko.bookingservice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hotel-service", path = "/internal/rooms")
public interface HotelServiceClient {

    @PostMapping("/{roomId}/confirm-availability")
    ResponseEntity<RoomInfo> confirmAvailability(
            @PathVariable UUID roomId);


    @PostMapping("/{hotelId}/find-available")
    ResponseEntity<List<RoomInfo>> findAvailableRooms(@PathVariable UUID hotelId);

    @PostMapping("/{roomId}/booked")
    ResponseEntity<Void> booked(@PathVariable UUID roomId);

    // DTO для ответа от hotel-service
    @Data
    class RoomInfo {
        private UUID id;
        private String number;
        private Integer timesBooked;
    }
}
