package org.mifistudy.grechko.bookingservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mifistudy.grechko.bookingservice.dto.BookingRequest;
import org.mifistudy.grechko.bookingservice.dto.BookingResponse;
import org.mifistudy.grechko.bookingservice.dto.UserResponse;
import org.mifistudy.grechko.bookingservice.entity.Booking;
import org.mifistudy.grechko.bookingservice.entity.User;
import org.mifistudy.grechko.bookingservice.repository.BookingRepository;
import org.mifistudy.grechko.bookingservice.repository.UserRepository;
import org.mifistudy.grechko.bookingservice.service.BookingService;
import org.mifistudy.grechko.bookingservice.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/")
@AllArgsConstructor
public class BookingController {

    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/status")
    public String status() {
        return "Booking Service is UP!";
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<User> allUsers = userRepository.findAll();
        List<UserResponse> users = allUsers
                .stream()
                .map(UserResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(users);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest request,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing token");
        }

        String token = authHeader.substring(7);

        UUID userId = UUID.fromString(jwtUtil.extractUserId(token));
        log.info("User {} creating booking", userId);

        try {
            Booking booking = bookingService.createBooking(userId, request);
            return ResponseEntity.ok(toResponse(booking));

        } catch (Exception e) {
            log.error("Failed to create booking", e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Failed to create booking: " + e.getMessage()
            );
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingResponse>> getUserBookings(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing token");
        }

        String token = authHeader.substring(7);

        UUID userId = UUID.fromString(jwtUtil.extractUserId(token));
        log.info("Getting bookings for user {}", userId);

        List<Booking> bookings = bookingRepository.findByUserId(userId);
        List<BookingResponse> responses = bookings.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponse> getBooking(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        log.info("User {} requesting booking {}", userId, id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));

        // Проверяем, что пользователь имеет доступ к этому бронированию
        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }

        return ResponseEntity.ok(toResponse(booking));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        log.info("User {} cancelling booking {}", userId, id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));

        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }

        // Отменяем бронирование
        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);

        return ResponseEntity.noContent().build();
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .roomId(booking.getRoomId())
                .hotelId(booking.getHotelId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .status(booking.getStatus())
                .build();
    }
}
