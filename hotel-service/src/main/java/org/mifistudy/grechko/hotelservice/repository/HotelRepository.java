package org.mifistudy.grechko.hotelservice.repository;

import org.mifistudy.grechko.hotelservice.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {
    Optional<Hotel> findByName(String name);
    Optional<Hotel> findByAddress(String address);
    boolean existsByName(String name);
    boolean existsByAddress(String address);
}
