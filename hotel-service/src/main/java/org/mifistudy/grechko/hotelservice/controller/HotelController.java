package org.mifistudy.grechko.hotelservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class HotelController {

    // In-memory хранилища для заглушек
    private List<Map<String, Object>> hotels = new ArrayList<>();
    private List<Map<String, Object>> rooms = new ArrayList<>();

    // Генератор ID
    private long hotelIdCounter = 1;
    private long roomIdCounter = 1;

    // === HOTEL OPERATIONS ===

    @PostMapping("/hotels")
    public ResponseEntity<?> addHotel(
            @RequestBody Map<String, String> hotelData,
            @RequestHeader("X-User-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        Map<String, Object> hotel = new HashMap<>();
        hotel.put("id", hotelIdCounter++);
        hotel.put("name", hotelData.getOrDefault("name", "Unknown Hotel"));
        hotel.put("location", hotelData.getOrDefault("location", "Unknown Location"));
        hotel.put("rating", hotelData.getOrDefault("rating", "4.0"));
        hotel.put("description", hotelData.getOrDefault("description", ""));

        hotels.add(hotel);

        return ResponseEntity.ok(Map.of(
                "message", "Hotel added (stub)",
                "hotelId", hotel.get("id"),
                "hotel", hotel
        ));
    }

    @GetMapping("/hotels")
    public ResponseEntity<?> getHotels(@RequestHeader("X-User-Id") String userId) {
        // Заглушечные данные для демонстрации
        if (hotels.isEmpty()) {
            hotels = Arrays.asList(
                    Map.of("id", 1, "name", "Grand Hotel", "location", "New York", "rating", 4.5, "description", "Luxury hotel in downtown"),
                    Map.of("id", 2, "name", "Seaside Resort", "location", "Miami", "rating", 4.2, "description", "Beachfront resort"),
                    Map.of("id", 3, "name", "Mountain Lodge", "location", "Colorado", "rating", 4.7, "description", "Ski-in ski-out hotel")
            );
        }

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "hotels", hotels
        ));
    }

    // === ROOM OPERATIONS ===

    @PostMapping("/rooms")
    public ResponseEntity<?> addRoom(
            @RequestBody Map<String, Object> roomData,
            @RequestHeader("X-User-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        Map<String, Object> room = new HashMap<>();
        room.put("id", roomIdCounter++);
        room.put("hotelId", roomData.getOrDefault("hotelId", 1));
        room.put("roomNumber", roomData.getOrDefault("roomNumber", "101"));
        room.put("roomType", roomData.getOrDefault("type", "STANDARD"));
        room.put("price", roomData.getOrDefault("price", 100.0));
        room.put("capacity", roomData.getOrDefault("capacity", 2));
        room.put("timesBooked", 0);
        room.put("available", true);

        rooms.add(room);

        return ResponseEntity.ok(Map.of(
                "message", "Room added (stub)",
                "roomId", room.get("id"),
                "room", room
        ));
    }

    @GetMapping("/rooms/recommend")
    public ResponseEntity<?> getRecommendedRooms(@RequestHeader("X-User-Id") String userId) {
        // Заглушечные данные для демонстрации
        if (rooms.isEmpty()) {
            generateMockRooms();
        }

        // Сортируем по timesBooked (по возрастанию) - самые редко бронируемые
        List<Map<String, Object>> recommendedRooms = new ArrayList<>(rooms);
        recommendedRooms.sort(Comparator.comparingInt(r -> (Integer) r.get("timesBooked")));

        // Фильтруем только доступные
        List<Map<String, Object>> availableRooms = recommendedRooms.stream()
                .filter(r -> (Boolean) r.get("available"))
                .toList();

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "sorting", "by times_booked (ascending)",
                "rooms", availableRooms
        ));
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getAvailableRooms(@RequestHeader("X-User-Id") String userId) {
        // Заглушечные данные для демонстрации
        if (rooms.isEmpty()) {
            generateMockRooms();
        }

        // Только доступные номера (без специальной сортировки)
        List<Map<String, Object>> availableRooms = rooms.stream()
                .filter(r -> (Boolean) r.get("available"))
                .toList();

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "rooms", availableRooms,
                "count", availableRooms.size()
        ));
    }

    // === INTERNAL OPERATIONS (для Saga Pattern) ===

    @PostMapping("/rooms/{id}/confirm-availability")
    public ResponseEntity<?> confirmAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Object> bookingDates) {

        // В реальной системе здесь блокировка номера на даты
        String checkIn = (String) bookingDates.getOrDefault("checkIn", "2024-12-01");
        String checkOut = (String) bookingDates.getOrDefault("checkOut", "2024-12-05");

        return ResponseEntity.ok(Map.of(
                "message", "Room availability confirmed (stub)",
                "roomId", id,
                "checkIn", checkIn,
                "checkOut", checkOut,
                "status", "TEMPORARILY_BLOCKED",
                "reservationToken", "reserve_" + UUID.randomUUID()
        ));
    }

    @PostMapping("/rooms/{id}/release")
    public ResponseEntity<?> releaseRoom(@PathVariable Long id) {
        // Компенсирующее действие для Saga
        return ResponseEntity.ok(Map.of(
                "message", "Room released from temporary block (stub)",
                "roomId", id,
                "status", "AVAILABLE"
        ));
    }

    // === STATUS ENDPOINT ===

    @GetMapping("/status")
    public String status() {
        return "Hotel Service is UP!";
    }

    // === HELPER METHODS ===

    private void generateMockRooms() {
        rooms = new ArrayList<>(Arrays.asList(
                Map.of("id", 1L, "hotelId", 1L, "roomNumber", "101", "type", "STANDARD",
                        "price", 100.0, "capacity", 2, "timesBooked", 5, "available", true),
                Map.of("id", 2L, "hotelId", 1L, "roomNumber", "102", "type", "DELUXE",
                        "price", 150.0, "capacity", 3, "timesBooked", 3, "available", true),
                Map.of("id", 3L, "hotelId", 2L, "roomNumber", "201", "type", "SUITE",
                        "price", 250.0, "capacity", 4, "timesBooked", 1, "available", true),
                Map.of("id", 4L, "hotelId", 2L, "roomNumber", "202", "type", "STANDARD",
                        "price", 120.0, "capacity", 2, "timesBooked", 7, "available", false),
                Map.of("id", 5L, "hotelId", 3L, "roomNumber", "301", "type", "DELUXE",
                        "price", 180.0, "capacity", 3, "timesBooked", 2, "available", true)
        ));
    }
}