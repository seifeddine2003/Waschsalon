package com.start.waschmachine.Reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService service;

    @PostMapping("/create")
    public Reservation create(@RequestBody ReservationRequest request) {
        return service.createReservation(request);
    }

    @GetMapping("/all")
    public List<Reservation> getAll() {
        return service.getAll();
    }
}
