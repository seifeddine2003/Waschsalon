package com.start.waschmachine.infrastructure.web;

import com.start.waschmachine.application.reservation.IReservationService;
import com.start.waschmachine.application.reservation.ReservationRequest;
import com.start.waschmachine.domain.reservation.Reservation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public List<Reservation> getByStudent(@PathVariable Integer studentId, Authentication authentication) {
        Integer authenticatedId = (Integer) authentication.getPrincipal();
        if (!authenticatedId.equals(studentId))
            throw new AccessDeniedException("You can only view your own reservations");
        return service.getByStudent(studentId);
    }

    @DeleteMapping("/{reservationId}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> cancel(
            @PathVariable Integer reservationId,
            Authentication authentication) {
        Integer studentId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(service.cancelReservation(reservationId, studentId));
    }
}
