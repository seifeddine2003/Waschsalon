package com.start.waschmachine.application.reservation;

import com.start.waschmachine.application.student.IStudentService;
import com.start.waschmachine.application.washmachine.IWashmachineService;
import com.start.waschmachine.domain.reservation.Reservation;
import com.start.waschmachine.domain.reservation.ReservationRepository;
import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.domain.washmachine.Washmachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReservationService implements IReservationService {

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private IWashmachineService washmachineService;

    @Transactional
    public Map<String, Object> createReservation(ReservationRequest req) {

        Student student = studentService.getStudent(req.getStudentId());
        if (student == null) throw new RuntimeException("Student not found");

        Washmachine machine = washmachineService.getById(req.getMachineId());

        LocalDate date = req.getDate();

        boolean exists = reservationRepo.isSlotTaken(
                req.getMachineId(), req.getStartTime(), date);

        if (exists) {
            throw new RuntimeException("This slot is already reserved for this machine");
        }

        double price = req.getPrice() != null ? req.getPrice() : 0.0;

        student = studentService.deductBalance(req.getStudentId(), price);

        Reservation reservation = new Reservation(
                student, machine, req.getStartTime(), req.getEndTime(), date);
        reservation.setWashType(req.getWashType());
        reservation.setWashDuration(req.getWashDuration());
        reservation.setPrice(price);

        reservationRepo.save(reservation);

        Map<String, Object> response = new HashMap<>();
        response.put("reservationId", reservation.getReservationId());
        response.put("newBalance", student.getBalance());
        response.put("washType", reservation.getWashType());
        response.put("startTime", reservation.getStartTime());
        response.put("endTime", reservation.getEndTime());
        response.put("date", reservation.getDate());
        response.put("price", reservation.getPrice());

        return response;
    }

    public List<Reservation> getAll() {
        return reservationRepo.findAll();
    }

    public List<Map<String, Object>> getAvailableSlots(Integer machineId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        List<Reservation> reservations = reservationRepo.findByMachineIdAndDate(machineId, today);

        Set<String> blockedSlots = new HashSet<>();
        for (Reservation r : reservations) {
            LocalTime resStart = LocalTime.parse(r.getStartTime(), fmt);
            LocalTime resEnd = resStart.plusMinutes(r.getWashDuration() + 15);
            LocalTime t = resStart;
            while (t.isBefore(resEnd)) {
                blockedSlots.add(t.format(fmt));
                t = t.plusMinutes(15);
            }
        }

        List<Map<String, Object>> availableSlots = new ArrayList<>();
        LocalTime start = LocalTime.of(6, 0);
        LocalTime end = LocalTime.of(23, 0);

        while (start.isBefore(end)) {
            LocalTime slotEnd = start.plusMinutes(15);
            String startStr = start.format(fmt);
            String endStr = slotEnd.format(fmt);

            if (start.isAfter(now) && !blockedSlots.contains(startStr)) {
                Map<String, Object> slot = new HashMap<>();
                slot.put("startTime", startStr);
                slot.put("endTime", endStr);
                availableSlots.add(slot);
            }

            start = slotEnd;
        }

        return availableSlots;
    }
}
