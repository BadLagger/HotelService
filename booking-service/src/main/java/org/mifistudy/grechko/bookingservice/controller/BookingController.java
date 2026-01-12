package org.mifistudy.grechko.bookingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
public class BookingController {

    private Map<String, Object> mockData = new HashMap<>();

    @GetMapping("/status")
    public String status() {
        return "Booking Service is UP!";
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return ResponseEntity.ok("User deleted");
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(
            @RequestBody Map<String, String> userData,
            @RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        mockData.put("newUser", userData);
        return ResponseEntity.ok(Map.of(
                "message", "User created (stub)",
                "userId", "user_" + System.currentTimeMillis()
        ));
    }

    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(
            @RequestBody Map<String, String> userData,
            @RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return ResponseEntity.ok("User updated (stub)");
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> credentials) {
        String token = "mock_token_" + UUID.randomUUID();
        return ResponseEntity.ok(Map.of(
                "message", "User registered (stub)",
                "token", token
        ));
    }

    @PostMapping("/user/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> credentials) {
        String token = "mock_token_" + UUID.randomUUID();
        return ResponseEntity.ok(Map.of(
                "message", "User authenticated (stub)",
                "token", token
        ));
    }

    @PostMapping("/booking")
    public ResponseEntity<?> createBooking(
            @RequestBody Map<String, Object> bookingRequest,
            @RequestHeader("X-User-Id") String userId) {

        boolean autoSelect = (Boolean) bookingRequest.getOrDefault("autoSelect", false);
        Long roomId = bookingRequest.containsKey("roomId") ?
                Long.valueOf(bookingRequest.get("roomId").toString()) : null;

        String bookingId = "booking_" + System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking created (stub)");
        response.put("bookingId", bookingId);
        response.put("userId", userId);
        response.put("autoSelect", autoSelect);
        if (!autoSelect && roomId != null) {
            response.put("selectedRoomId", roomId);
        } else {
            response.put("autoSelectedRoomId", 101L); // Заглушка
        }
        response.put("status", "CONFIRMED");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getUserBookings(@RequestHeader("X-User-Id") String userId) {
        List<Map<String, Object>> bookings = Arrays.asList(
                Map.of(
                        "id", "booking_1",
                        "userId", userId,
                        "roomId", 101,
                        "checkIn", "2024-12-01",
                        "checkOut", "2024-12-05",
                        "status", "CONFIRMED"
                ),
                Map.of(
                        "id", "booking_2",
                        "userId", userId,
                        "roomId", 205,
                        "checkIn", "2024-11-15",
                        "checkOut", "2024-11-20",
                        "status", "COMPLETED"
                )
        );

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "bookings", bookings
        ));
    }

    @GetMapping("/booking/{id}")
    public ResponseEntity<?> getBookingById(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {

        return ResponseEntity.ok(Map.of(
                "id", id,
                "userId", userId,
                "roomId", 101,
                "checkIn", "2024-12-01",
                "checkOut", "2024-12-05",
                "status", "CONFIRMED",
                "message", "Booking details (stub)"
        ));
    }

    @DeleteMapping("/booking/{id}")
    public ResponseEntity<?> cancelBooking(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {

        return ResponseEntity.ok(Map.of(
                "message", "Booking cancelled (stub)",
                "bookingId", id,
                "userId", userId,
                "status", "CANCELLED"
        ));
    }
}
