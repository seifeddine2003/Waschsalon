package com.start.waschmachine.Reservation;

import com.start.waschmachine.Student.Student;
import com.start.waschmachine.Student.StudentRepository;
import com.start.waschmachine.Washmachine.Washmachine;
import com.start.waschmachine.Washmachine.WashmachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private WashmachineRepository machineRepo;

    public Reservation createReservation(ReservationRequest req) {

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

        Reservation reservation = new Reservation(
                student, machine, req.getStartTime(), req.getEndTime(), date);

        reservation.setWashType(req.getWashType());
        reservation.setWashDuration(req.getWashDuration());

        return reservationRepo.save(reservation);
    }

    public List<Reservation> getAll() {
        return reservationRepo.findAll();
    }
}