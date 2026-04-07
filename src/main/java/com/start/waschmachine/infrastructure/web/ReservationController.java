package com.start.waschmachine.infrastructure.web;

import com.start.waschmachine.application.reservation.IReservationService;
import com.start.waschmachine.application.reservation.ReservationRequest;
import com.start.waschmachine.domain.reservation.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private IReservationService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ReservationRequest request) {
        try {
            Map<String, Object> result = service.createReservation(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Reservation> getAll() {
        return service.getAll();
    }
}
