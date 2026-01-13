package org.mifistudy.grechko.bookingservice.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mifistudy.grechko.bookingservice.entity.User;
import org.mifistudy.grechko.bookingservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        String adminUsername = "admin";

        // Проверяем, существует ли уже админ
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@hotel.com")
                    .role(User.Role.ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("✅ Default admin user created!");
            log.info("👑 Username: {}", adminUsername);
            log.info("🔑 Password: admin123");
            log.info("📧 Email: admin@hotel.com");
        } else {
            log.info("ℹ️ Admin user already exists");
        }
    }
}
