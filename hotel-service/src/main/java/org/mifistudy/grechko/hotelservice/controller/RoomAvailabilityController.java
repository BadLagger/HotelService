package org.mifistudy.grechko.hotelservice.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mifistudy.grechko.hotelservice.dto.AvailabilityRequest;
import org.mifistudy.grechko.hotelservice.dto.AvailabilityResponse;
import org.mifistudy.grechko.hotelservice.entity.Room;
import org.mifistudy.grechko.hotelservice.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/internal/rooms")
@RequiredArgsConstructor
public class RoomAvailabilityController {

    private final RoomRepository roomRepository;

    @PostMapping("/{roomId}/confirm-availability")
    public ResponseEntity<Room> confirmAvailability(
            @PathVariable UUID roomId) {

        log.info("Confirming availability for hotelId: {}", roomId);

        Room result = roomRepository.findById(roomId).orElseThrow();

        if (!result.getAvailable()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{hotelId}/find-available")
    public ResponseEntity<List<Room>> findAvailableRooms(
            @PathVariable UUID hotelId) {

        log.info("Find available for hotelId: {}", hotelId);

        List<Room> room = roomRepository.findByHotelIdAndAvailableTrue(hotelId);

        return ResponseEntity.ok(room);
    }

    @PostMapping("/{roomId}/booked")
    public ResponseEntity<Void> booked(@PathVariable UUID roomId) {

        roomRepository.incrementTimesBooked(roomId);

        return ResponseEntity.ok().build();
    }
}
