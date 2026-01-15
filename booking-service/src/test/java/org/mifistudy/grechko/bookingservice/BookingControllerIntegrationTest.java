package org.mifistudy.grechko.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mifistudy.grechko.bookingservice.testconfig.TestSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Добавь эту аннотацию!
@Import(TestSecurityConfig.class)
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> bookingRequest;
    private Map<String, String> authRequest;

    @BeforeEach
    void setUp() {
        /*bookingRequest = new HashMap<>();
        bookingRequest.put("roomId", 101);
        bookingRequest.put("autoSelect", false);
        bookingRequest.put("checkIn", "2024-12-01");
        bookingRequest.put("checkOut", "2024-12-05");

        authRequest = new HashMap<>();
        authRequest.put("email", "test@test.com");
        authRequest.put("password", "password123");*/
    }

    // === AUTHENTICATION TESTS ===

    @Test
    void testStatus() throws Exception {
        mockMvc.perform(post("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking Service is UP!"));
    }

    /*@Test
    void testRegisterUser() throws Exception {
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered (stub)"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testAuthenticateUser() throws Exception {
        mockMvc.perform(post("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    // === BOOKING TESTS ===

    @Test
    void testCreateBookingWithRoomId() throws Exception {
        mockMvc.perform(post("/booking")
                        .header("X-User-Id", "user_123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.autoSelect").value(false))
                .andExpect(jsonPath("$.selectedRoomId").value(101));
    }

    @Test
    void testCreateBookingWithAutoSelect() throws Exception {
        bookingRequest.put("autoSelect", true);
        bookingRequest.remove("roomId");

        mockMvc.perform(post("/booking")
                        .header("X-User-Id", "user_123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autoSelect").value(true))
                .andExpect(jsonPath("$.autoSelectedRoomId").exists());
    }

    @Test
    void testGetUserBookings() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-User-Id", "user_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user_123"))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.bookings.length()").value(2));
    }

    @Test
    void testGetBookingById() throws Exception {
        mockMvc.perform(get("/booking/booking_123")
                        .header("X-User-Id", "user_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("booking_123"))
                .andExpect(jsonPath("$.userId").value("user_123"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testCancelBooking() throws Exception {
        mockMvc.perform(delete("/booking/booking_123")
                        .header("X-User-Id", "user_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("booking_123"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    // === ADMIN TESTS ===

    @Test
    void testCreateUser_AsAdmin() throws Exception {
        Map<String, String> userData = new HashMap<>();
        userData.put("name", "John Doe");
        userData.put("email", "john@doe.com");

        mockMvc.perform(post("/user")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists());
    }

    @Test
    void testUpdateUser() throws Exception {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "updated@test.com");

        mockMvc.perform(patch("/user")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/user")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk());
    }*/
}