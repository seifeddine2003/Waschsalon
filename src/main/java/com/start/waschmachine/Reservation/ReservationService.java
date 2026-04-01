package com.start.waschmachine.Reservation;

import com.start.waschmachine.Student.Student;
import com.start.waschmachine.Student.StudentRepository;
import com.start.waschmachine.Washmachine.Washmachine;
import com.start.waschmachine.Washmachine.WashmachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private WashmachineRepository machineRepo;

    @Transactional
    public Map<String, Object> createReservation(ReservationRequest req) {

        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Washmachine machine = machineRepo.findById(req.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        LocalDate date = req.getDate();

        boolean exists = reservationRepo.isSlotTaken(
                req.getMachineId(), req.getStartTime(), date);

        if (exists) {
            throw new RuntimeException("This slot is already reserved for this machine");
        }

        // Check balance
        double price = req.getPrice() != null ? req.getPrice() : 0.0;
        if (student.getBalance() < price) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct balance
        student.setBalance(student.getBalance() - price);
        studentRepo.save(student);

        // Create reservation
        Reservation reservation = new Reservation(
                student, machine, req.getStartTime(), req.getEndTime(), date);
        reservation.setWashType(req.getWashType());
        reservation.setWashDuration(req.getWashDuration());
        reservation.setPrice(price);

        reservationRepo.save(reservation);

        // Return reservation + new balance
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
}