package com.start.waschmachine.Reservation;

import com.start.waschmachine.Slot.Slot;
import com.start.waschmachine.Washmachine.Washmachine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ReservationRepository
        extends JpaRepository<Reservation, Integer> {

    boolean existsByWashmachineAndSlotAndDate(
            Washmachine washmachine,
            Slot slot,
            LocalDate date
    );
}
