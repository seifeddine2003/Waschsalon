package com.start.waschmachine.Reservation;

import com.start.waschmachine.Student.Student;
import com.start.waschmachine.Student.StudentRepository;
import com.start.waschmachine.Washmachine.Washmachine;
import com.start.waschmachine.Washmachine.WashmachineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WashmachineRepository washmachineRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Washmachine machine;
    private Student student;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        machine = washmachineRepository.save(
                new Washmachine("M-01", "active", null, null, true)
        );
        student = studentRepository.save(
                new Student("pass123", "john@example.com", "Doe", "John")
        );
        today = LocalDate.now();
    }

    private Reservation makeReservation(Washmachine m, String start, String end) {
        Reservation r = new Reservation(student, m, start, end, today);
        r.setWashDuration(30);
        return r;
    }

    @Test
    void isSlotTaken_shouldReturnTrue_whenActiveReservationExists() {
        reservationRepository.save(makeReservation(machine, "08:00", "08:30"));

        boolean taken = reservationRepository
                .isSlotTaken(machine.getMachineId(), "08:00", today);

        assertThat(taken).isTrue();
    }

    @Test
    void isSlotTaken_shouldReturnFalse_whenNoReservationExists() {
        boolean taken = reservationRepository
                .isSlotTaken(machine.getMachineId(), "09:00", today);

        assertThat(taken).isFalse();
    }

    @Test
    void isSlotTaken_shouldReturnFalse_whenReservationIsCancelled() {
        Reservation r = makeReservation(machine, "08:00", "08:30");
        r.setStatus("cancelled");
        reservationRepository.save(r);

        boolean taken = reservationRepository
                .isSlotTaken(machine.getMachineId(), "08:00", today);

        assertThat(taken).isFalse();
    }

    @Test
    void isSlotTaken_shouldReturnFalse_whenDifferentDateIsUsed() {
        reservationRepository.save(makeReservation(machine, "08:00", "08:30"));

        boolean taken = reservationRepository
                .isSlotTaken(machine.getMachineId(), "08:00", today.plusDays(1));

        assertThat(taken).isFalse();
    }

    @Test
    void isSlotTaken_shouldReturnFalse_whenDifferentMachineIsUsed() {
        Washmachine otherMachine = washmachineRepository.save(
                new Washmachine("M-02", "active", null, null, true)
        );
        reservationRepository.save(makeReservation(machine, "08:00", "08:30"));

        boolean taken = reservationRepository
                .isSlotTaken(otherMachine.getMachineId(), "08:00", today);

        assertThat(taken).isFalse();
    }

    @Test
    void findByMachineIdAndDate_shouldReturnActiveReservations() {
        reservationRepository.save(makeReservation(machine, "08:00", "08:30"));

        List<Reservation> results = reservationRepository
                .findByMachineIdAndDate(machine.getMachineId(), today);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStartTime()).isEqualTo("08:00");
    }

    @Test
    void findByMachineIdAndDate_shouldExcludeCancelledReservations() {
        Reservation r = makeReservation(machine, "08:00", "08:30");
        r.setStatus("cancelled");
        reservationRepository.save(r);

        List<Reservation> results = reservationRepository
                .findByMachineIdAndDate(machine.getMachineId(), today);

        assertThat(results).isEmpty();
    }

    @Test
    void findByMachineIdAndDate_shouldReturnEmpty_whenNoReservationsForDate() {
        List<Reservation> results = reservationRepository
                .findByMachineIdAndDate(machine.getMachineId(), today.plusDays(5));

        assertThat(results).isEmpty();
    }

    @Test
    void findByMachineIdAndDate_shouldReturnMultipleReservations() {
        reservationRepository.save(makeReservation(machine, "08:00", "08:30"));
        reservationRepository.save(makeReservation(machine, "09:00", "09:30"));

        List<Reservation> results = reservationRepository
                .findByMachineIdAndDate(machine.getMachineId(), today);

        assertThat(results).hasSize(2);
    }

    @Test
    void findByMachineIdAndDate_shouldNotReturnOtherMachinesReservations() {
        Washmachine otherMachine = washmachineRepository.save(
                new Washmachine("M-02", "active", null, null, true)
        );
        reservationRepository.save(makeReservation(otherMachine, "08:00", "08:30"));

        List<Reservation> results = reservationRepository
                .findByMachineIdAndDate(machine.getMachineId(), today);

        assertThat(results).isEmpty();
    }

}