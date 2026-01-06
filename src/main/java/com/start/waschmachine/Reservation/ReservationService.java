package com.start.waschmachine.Reservation;

import com.start.waschmachine.Slot.Slot;
import com.start.waschmachine.Slot.SlotRepository;
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

    @Autowired
    private SlotRepository slotRepo;

    public Reservation createReservation(ReservationRequest req) {

        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Washmachine machine = machineRepo.findById(req.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        Slot slot = slotRepo.findById(req.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        LocalDate date = req.getDate();

        boolean exists = reservationRepo
                .existsByWashmachineAndSlotAndDate(machine, slot, date);

        if (exists) {
            throw new RuntimeException("This slot is already reserved for this machine");
        }

        Reservation reservation =
                new Reservation(student, machine, slot, date);

        return reservationRepo.save(reservation);
    }
    public List<Reservation> getAll() {
        return reservationRepo.findAll();
    }

}
