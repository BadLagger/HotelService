package org.mifistudy.grechko.hotelservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mifistudy.grechko.hotelservice.dto.HotelRequest;
import org.mifistudy.grechko.hotelservice.entity.Hotel;
import org.mifistudy.grechko.hotelservice.repository.HotelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hotels")
@AllArgsConstructor
public class HotelsController {

    private final HotelRepository hotelRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hotel> createHotel(@RequestBody HotelRequest request,
                                      @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaim("usedId");
        String role = jwt.getClaim("role");

        log.debug("UserId from jwt: {}", userId);
        log.debug("Role from jwt: {}", role);

        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .address(request.getAddress())
                .build();

        Hotel savedHotel = hotelRepository.save(hotel);

        return ResponseEntity.ok(savedHotel);
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.ok(hotelRepository.findAll());
    }

    @GetMapping("/status")
    public String status() {
        return "Hotel Service is UP!";
    }
}
