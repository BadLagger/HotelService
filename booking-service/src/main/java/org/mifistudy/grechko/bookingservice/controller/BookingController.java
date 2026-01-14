package org.mifistudy.grechko.bookingservice.controller;

import lombok.AllArgsConstructor;
import org.mifistudy.grechko.bookingservice.dto.UserResponse;
import org.mifistudy.grechko.bookingservice.entity.User;
import org.mifistudy.grechko.bookingservice.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class BookingController {

    private final UserRepository userRepository;

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
}
