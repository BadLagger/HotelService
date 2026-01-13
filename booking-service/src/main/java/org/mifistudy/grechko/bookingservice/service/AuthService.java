package org.mifistudy.grechko.bookingservice.service;

import lombok.RequiredArgsConstructor;
import org.mifistudy.grechko.bookingservice.dto.AuthRequest;
import org.mifistudy.grechko.bookingservice.dto.AuthResponse;
import org.mifistudy.grechko.bookingservice.dto.UserRequest;
import org.mifistudy.grechko.bookingservice.entity.User;
import org.mifistudy.grechko.bookingservice.repository.UserRepository;
import org.mifistudy.grechko.bookingservice.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if ((request.getRole() != null) && (request.getRole() == User.Role.ADMIN)) {
            throw new RuntimeException("Only role USER is allowed!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(User.Role.USER) // При регистрации всегда USER
                .build();

        userRepository.save(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        Optional<User> user = userRepository.findByUsername(request.getUsername());
        String role = user.map(u -> u.getRole().name()).orElse("USER");

        return new AuthResponse(token, request.getUsername(), role);
    }
}
