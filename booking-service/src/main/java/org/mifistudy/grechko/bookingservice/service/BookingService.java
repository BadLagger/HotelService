package org.mifistudy.grechko.bookingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mifistudy.grechko.bookingservice.client.HotelServiceClient;
import org.mifistudy.grechko.bookingservice.dto.BookingRequest;
import org.mifistudy.grechko.bookingservice.entity.Booking;
import org.mifistudy.grechko.bookingservice.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelServiceClient hotelServiceClient;

    @Transactional
    public Booking createBooking(UUID userId, BookingRequest request) {

        Booking savedBooking = null;

        log.info("Creating booking for user {}: {}", userId, request);

        UUID roomId;
        UUID hotelId = request.getHotelId();

        if (request.isAutoSelect()) {
            // Автоподбор доступной комнаты
            log.info("Autoselect");
            try {
                ResponseEntity<List<HotelServiceClient.RoomInfo>> availableRoomsResponse = hotelServiceClient.findAvailableRooms(hotelId);

                if (!availableRoomsResponse.getStatusCode().is2xxSuccessful()) {
                    log.error("Error response from hotel-service: {}", availableRoomsResponse.getStatusCode());
                    throw new RuntimeException("Error response from hotel-service");
                }

                var availableRooms = availableRoomsResponse.getBody();

                if ((availableRooms == null) || availableRooms.isEmpty()) {
                    log.error("Empty list of available rooms");
                    throw new RuntimeException("Empty list of available rooms");
                }

                availableRooms.sort(Comparator.comparing(HotelServiceClient.RoomInfo::getTimesBooked));
                HotelServiceClient.RoomInfo choose = null;

                for (var room : availableRooms) {
                    log.info("Check Room {}", room.getNumber());
                    if (bookingRepository.existsByHotelIdAndRoomId(hotelId, room.getId())) {
                        log.info("Room {} is in bookings! Try check for status", room.getNumber());
                        List<Booking.Status> statuses = List.of(Booking.Status.CONFIRMED, Booking.Status.PENDING);
                        if (!bookingRepository.existsOverlappingBooking(hotelId, room.getId(),
                                statuses,
                                request.getStartDate(), request.getEndDate())) {
                            choose = room;
                            break;
                        }
                    } else {
                        log.info("Room {} is not presented in bookings", room.getNumber());
                        choose = room;
                        break;
                    }
                }

                if (choose == null) {
                    log.error("All rooms are busy in the hotel!");
                    throw new RuntimeException("All rooms are busy in the hotel!");
                }

                Booking booking = Booking
                        .builder()
                        .userId(userId)
                        .hotelId(hotelId)
                        .roomId(choose.getId())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .status(Booking.Status.CONFIRMED)
                        .build();

                savedBooking = bookingRepository.save(booking);

                ResponseEntity<Void> response = hotelServiceClient.booked(choose.getId());

                if (!response.getStatusCode().is2xxSuccessful()) {
                    log.error("Can't increase times booked");
                }

            } catch(Exception e) {
                log.error("Can't get available room: {}", e.getMessage());
                throw new RuntimeException("Can't get available room!");
            }
        } else {
            log.info("Select manual");
            roomId = request.getRoomId();

            if (bookingRepository.existsByHotelIdAndRoomId(hotelId, roomId)) {
                log.info("Room and hotel exists");
                List<Booking.Status> statuses = List.of(Booking.Status.CONFIRMED, Booking.Status.PENDING);
                if (bookingRepository.existsOverlappingBooking(hotelId, roomId,
                        statuses,
                        request.getStartDate(), request.getEndDate())) {
                    throw new RuntimeException("Rooms already busy!");
                }
            }

                // Временно блокируем номер на проверку доступности
                Booking booking = Booking
                        .builder()
                        .userId(userId)
                        .hotelId(hotelId)
                        .roomId(roomId)
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .status(Booking.Status.PENDING)
                        .build();

                savedBooking = bookingRepository.save(booking);

                try {
                    ResponseEntity<HotelServiceClient.RoomInfo> response = hotelServiceClient.confirmAvailability(roomId);

                    if (!response.getStatusCode().is2xxSuccessful()) {
                        log.error("Error response from hotel-service: {}", response.getStatusCode());
                        throw new RuntimeException("Error response from hotel-service");
                    }

                    HotelServiceClient.RoomInfo roomInfo = response.getBody();

                    if (roomInfo != null) {
                        booking.setStatus(Booking.Status.CONFIRMED);
                        bookingRepository.updateBookingStatus(savedBooking.getId(), Booking.Status.CONFIRMED);
                        ResponseEntity<Void> responseBooked = hotelServiceClient.booked(savedBooking.getId());

                        if (!responseBooked.getStatusCode().is2xxSuccessful()) {
                            log.error("Can't increase times booked");
                        }

                    } else {
                        booking.setStatus(Booking.Status.CANCELLED);
                        bookingRepository.updateBookingStatus(savedBooking.getId(), Booking.Status.CANCELLED);
                    }
                } catch (Exception e) {
                    log.error("Booking request {} has failed: {}", savedBooking.getId(), e.getMessage());
                    bookingRepository.updateBookingStatus(savedBooking.getId(), Booking.Status.CANCELLED);
                    throw new RuntimeException("Service Connection Timeout");
                }
        }

        if (savedBooking != null) {
            log.info("{} booking successful created!", savedBooking.getId());
        } else {
            log.error("Booking creation error");
            throw new RuntimeException("Booking creation error");
        }

        return savedBooking;
    }
}
