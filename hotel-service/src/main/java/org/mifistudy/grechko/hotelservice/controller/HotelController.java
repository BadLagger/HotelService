package org.mifistudy.grechko.hotelservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HotelController {

    @GetMapping("/status")
    public String status() {
        return "Hotel Service is UP!";
    }
}
