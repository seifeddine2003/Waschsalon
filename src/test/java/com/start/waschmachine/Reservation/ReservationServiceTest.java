package com.start.waschmachine.Reservation;

import com.start.waschmachine.application.reservation.ReservationRequest;
import com.start.waschmachine.application.reservation.ReservationService;
import com.start.waschmachine.application.student.IStudentService;
import com.start.waschmachine.application.washmachine.IWashmachineService;
import com.start.waschmachine.domain.reservation.ReservationRepository;
import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.domain.washmachine.Washmachine;
import com.start.waschmachine.exception.StudentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepo;
    @Mock private IStudentService studentService;
    @Mock private IWashmachineService washmachineService;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservation_success() {
        Student student = new Student("password", "test@email.com", "Doe", "John");
        student.setBalance(BigDecimal.valueOf(10));
        Washmachine machine = new Washmachine("M1", "Available", null, null, true);

        ReservationRequest req = new ReservationRequest();
        req.setStudentId(1);
        req.setMachineId(1);
        req.setStartTime("10:00");
        req.setEndTime("10:30");
        req.setDate(LocalDate.now());
        req.setWashType("Quick Wash");
        req.setWashDuration(30);
        req.setPrice(BigDecimal.valueOf(2));

        when(studentService.getStudent(1)).thenReturn(student);
        when(washmachineService.getById(1)).thenReturn(machine);
        when(reservationRepo.isSlotTaken(1, "10:00", LocalDate.now())).thenReturn(false);
        when(studentService.deductBalance(1, BigDecimal.valueOf(2))).thenReturn(student);
        when(reservationRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> result = reservationService.createReservation(req);

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

        when(studentService.getStudent(1)).thenReturn(student);
        when(washmachineService.getById(1)).thenReturn(machine);
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

        when(studentService.getStudent(99)).thenThrow(new StudentNotFoundException(99));

        assertThrows(StudentNotFoundException.class,
                () -> reservationService.createReservation(req));
    }

    @Test
    void createReservation_machineNotFound_throwsException() {
        Student student = new Student("password123", "test@email.com", "Doe", "John");

        ReservationRequest req = new ReservationRequest();
        req.setStudentId(1);
        req.setMachineId(99);
        req.setDate(LocalDate.now());

        when(studentService.getStudent(1)).thenReturn(student);
        when(washmachineService.getById(99)).thenThrow(new RuntimeException("Washmachine not found"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(req));

        assertEquals("Washmachine not found", ex.getMessage());
        verify(reservationRepo, never()).save(any());
    }
}
