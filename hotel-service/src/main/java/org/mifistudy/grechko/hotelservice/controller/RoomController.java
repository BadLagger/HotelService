package org.mifistudy.grechko.hotelservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mifistudy.grechko.hotelservice.dto.AvailabilityRequest;
import org.mifistudy.grechko.hotelservice.dto.AvailabilityResponse;
import org.mifistudy.grechko.hotelservice.dto.RoomRequest;
import org.mifistudy.grechko.hotelservice.dto.RoomResponse;
import org.mifistudy.grechko.hotelservice.entity.Hotel;
import org.mifistudy.grechko.hotelservice.entity.Room;
import org.mifistudy.grechko.hotelservice.repository.HotelRepository;
import org.mifistudy.grechko.hotelservice.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(@RequestBody RoomRequest request) {
        log.info("Creating room for hotel: {}", request.getHotelId());

        // Находим отель
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Hotel not found with id: " + request.getHotelId()
                ));

        // Проверяем, нет ли уже комнаты с таким номером в отеле
        boolean roomExists = roomRepository.findByHotelIdAndNumber(
                request.getHotelId(),
                request.getNumber()
        ).isPresent();

        if (roomExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room with number " + request.getNumber() + " already exists in hotel"
            );
        }
        // Создаем комнату
        Room room = Room.builder()
                .hotel(hotel)
                .number(request.getNumber())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .timesBooked(0)
                .build();

        Room savedRoom = roomRepository.save(room);
        log.info("Room created: {} in hotel {}", savedRoom.getNumber(), hotel.getName());

        return ResponseEntity.ok(toResponse(savedRoom));
    }

    // GET /api/rooms/recommend — рекомендованные номера (USER)
    @GetMapping("/recommend")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RoomResponse>> getRecommendedRooms() {
        log.info("Getting recommended rooms");

        List<Room> rooms = roomRepository.findByAvailableTrueOrderByTimesBookedAsc();
        List<RoomResponse> response = rooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        log.info("Found {} recommended rooms", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms() {
        log.info("Getting all available rooms");

        List<Room> rooms = roomRepository.findByAvailableTrue();
        List<RoomResponse> response = rooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        log.info("Found {} available rooms", response.size());
        return ResponseEntity.ok(response);
    }

    private RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .hotelName(room.getHotel().getName())
                .number(room.getNumber())
                .available(room.getAvailable())
                .timesBooked(room.getTimesBooked())
                .build();
    }
}
