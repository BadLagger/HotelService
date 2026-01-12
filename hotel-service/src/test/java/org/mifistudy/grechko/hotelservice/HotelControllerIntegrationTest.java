package org.mifistudy.grechko.hotelservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mifistudy.grechko.hotelservice.testconfig.TestSecurityConfig;
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
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class HotelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> hotelData;
    private Map<String, Object> roomData;

    @BeforeEach
    void setUp() {
        hotelData = new HashMap<>();
        hotelData.put("name", "Test Hotel");
        hotelData.put("location", "Test City");
        hotelData.put("rating", "4.5");
        hotelData.put("description", "Test description");

        roomData = new HashMap<>();
        roomData.put("hotelId", 1);
        roomData.put("roomNumber", "101");
        roomData.put("type", "STANDARD");
        roomData.put("price", 100.0);
        roomData.put("capacity", 2);
    }

    // === HOTEL TESTS ===

    @Test
    void testAddHotel_AsAdmin() throws Exception {
        mockMvc.perform(post("/api/hotels")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hotel added (stub)"))
                .andExpect(jsonPath("$.hotelId").exists());
    }

    @Test
    void testAddHotel_WithoutAdminRole() throws Exception {
        mockMvc.perform(post("/api/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHotels() throws Exception {
        mockMvc.perform(get("/api/hotels")
                        .header("X-User-Id", "user_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user_123"))
                .andExpect(jsonPath("$.hotels").isArray())
                .andExpect(jsonPath("$.hotels.length()").value(1));
    }

    // === ROOM TESTS ===

    @Test
    void testAddRoom_AsAdmin() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room added (stub)"))
                .andExpect(jsonPath("$.roomId").exists());
    }

    @Test
    void testGetRecommendedRooms() throws Exception {
        mockMvc.perform(get("/api/rooms/recommend")
                        .header("X-User-Id", "user_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user_123"))
                .andExpect(jsonPath("$.sorting").value("by times_booked (ascending)"))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.rooms.length()").value(4)); // 4 доступных из 5
    }

    @Test
    void testGetAvailableRooms() throws Exception {
        mockMvc.perform(get("/api/rooms")
                        .header("X-User-Id", "user_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user_123"))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.count").value(4));
    }

    // === INTERNAL OPERATIONS TESTS ===

    @Test
    void testConfirmAvailability() throws Exception {
        Map<String, String> bookingDates = new HashMap<>();
        bookingDates.put("checkIn", "2024-12-01");
        bookingDates.put("checkOut", "2024-12-05");

        mockMvc.perform(post("/api/rooms/1/confirm-availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room availability confirmed (stub)"))
                .andExpect(jsonPath("$.roomId").value(1))
                .andExpect(jsonPath("$.status").value("TEMPORARILY_BLOCKED"))
                .andExpect(jsonPath("$.reservationToken").exists());
    }

    @Test
    void testReleaseRoom() throws Exception {
        mockMvc.perform(post("/api/rooms/1/release"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room released from temporary block (stub)"))
                .andExpect(jsonPath("$.roomId").value(1))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    // === STATUS ENDPOINT ===

    @Test
    void testStatusEndpoint() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hotel Service is UP")));
    }
}