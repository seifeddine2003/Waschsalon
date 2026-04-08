package com.start.waschmachine.application.reservation;

import com.start.waschmachine.domain.reservation.Reservation;

import java.util.List;
import java.util.Map;

public interface IReservationService {
    Map<String, Object> createReservation(ReservationRequest req);
    List<Reservation> getAll();
    List<Reservation> getByStudent(Integer studentId);
    List<Map<String, Object>> getAvailableSlots(Integer machineId);
}
