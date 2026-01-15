package org.mifistudy.grechko.hotelservice.repository;

import org.mifistudy.grechko.hotelservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    // Найти все комнаты отеля
    List<Room> findByHotelId(UUID hotelId);

    // Найти все доступные комнаты
    List<Room> findByAvailableTrue();

    // Найти все доступные комнаты отеля
    List<Room> findByHotelIdAndAvailableTrue(UUID hotelId);

    // Найти комнату по номеру в отеле
    Optional<Room> findByHotelIdAndNumber(UUID hotelId, String number);

    // Рекомендованные комнаты (доступные, отсортированные по timesBooked)
    List<Room> findByAvailableTrueOrderByTimesBookedAsc();

    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.timesBooked = r.timesBooked + 1 WHERE r.id = :id")
    void incrementTimesBooked(@Param("id") UUID id);
}
