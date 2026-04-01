package com.start.waschmachine.Reservation;

import com.start.waschmachine.Student.Student;
import com.start.waschmachine.Student.StudentRepository;
import com.start.waschmachine.Washmachine.Washmachine;
import com.start.waschmachine.Washmachine.WashmachineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepo;
    @Mock private StudentRepository studentRepo;
    @Mock private WashmachineRepository machineRepo;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservation_success() {
        Student student = new Student("password", "test@email.com", "Doe", "John");
        Washmachine machine = new Washmachine("M1", "Available", null, null, true);

        ReservationRequest req = new ReservationRequest();
        req.setStudentId(1);
        req.setMachineId(1);
        req.setStartTime("10:00");
        req.setEndTime("10:30");
        req.setDate(LocalDate.now());
        req.setWashType("Quick Wash");
        req.setWashDuration(30);

        when(studentRepo.findById(1)).thenReturn(Optional.of(student));
        when(machineRepo.findById(1)).thenReturn(Optional.of(machine));
        when(reservationRepo.isSlotTaken(1, "10:00", LocalDate.now())).thenReturn(false);
        when(reservationRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(studentRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> result = reservationService.createReservatit adon(req);

        assertNotNull(result);
        assertEquals("10:00", result.get("startTime"));
        assertEquals("10:30", result.get("endTime"));
        assertEquals("Quick Wash", result.get("washType"));
    }

    @Test
    void createReservation_slotAlreadyTaken_throwsException() {
        Student student = new Student("password", "test@email.com", "Doe", "John");
        Washmachine machine = new Washmachine("M1", "Available", null, null, true);

        ReservationRequest req = new ReservationRequest();
        req.setStudentId(1);
        req.setMachineId(1);
        req.setStartTime("10:00");
        req.setEndTime("10:30");
        req.setDate(LocalDate.now());
        req.setWashType("Quick Wash");
        req.setWashDuration(30);

        when(studentRepo.findById(1)).thenReturn(Optional.of(student));
        when(machineRepo.findById(1)).thenReturn(Optional.of(machine));
        when(reservationRepo.isSlotTaken(1, "10:00", LocalDate.now())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(req));

        assertEquals("This slot is already reserved for this machine", ex.getMessage());
        verify(reservationRepo, never()).save(any());
    }

    @Test
    void createReservation_studentNotFound_throwsException() {
        ReservationRequest req = new ReservationRequest();
        req.setStudentId(99);
        req.setMachineId(1);
        req.setDate(LocalDate.now());

        when(studentRepo.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(req));

        assertEquals("Student not found", ex.getMessage());
    }

    @Test
    void createReservation_machineNotFound_throwsException() {
        Student student = new Student("password123", "test@email.com", "Doe", "John");

        ReservationRequest req = new ReservationRequest();
        req.setStudentId(1);
        req.setMachineId(99);
        req.setDate(LocalDate.now());

        when(studentRepo.findById(1)).thenReturn(Optional.of(student));
        when(machineRepo.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(req));

        assertEquals("Machine not found", ex.getMessage());
        verify(reservationRepo, never()).save(any());
    }
}
