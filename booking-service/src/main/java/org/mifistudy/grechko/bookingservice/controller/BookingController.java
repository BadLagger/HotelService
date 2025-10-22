package org.mifistudy.grechko.bookingservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @GetMapping("/status")
    public String status() {
        return "Booking Service is UP!";
    }
}
