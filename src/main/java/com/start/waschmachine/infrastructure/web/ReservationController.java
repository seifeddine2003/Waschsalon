package com.start.waschmachine.infrastructure.web;

import com.start.waschmachine.application.reservation.IReservationService;
import com.start.waschmachine.application.reservation.ReservationRequest;
import com.start.waschmachine.domain.reservation.Reservation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private IReservationService service;

    @PostMapping("/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(service.createReservation(request));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getAll() {
        return service.getAll();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Reservation> getByStudent(@PathVariable Integer studentId) {
        return service.getByStudent(studentId);
    }

    @DeleteMapping("/{reservationId}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> cancel(
            @PathVariable Integer reservationId,
            @RequestParam Integer studentId) {
        return ResponseEntity.ok(service.cancelReservation(reservationId, studentId));
    }
}
