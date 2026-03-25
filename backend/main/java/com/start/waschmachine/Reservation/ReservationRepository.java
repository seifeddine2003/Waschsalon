package com.start.waschmachine.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    boolean existsByWashmachine_MachineIdAndStartTimeAndDate(
            Integer machineId,
            String startTime,
            LocalDate date
    );
}