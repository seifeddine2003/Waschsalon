package com.start.waschmachine.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reservation r " +
            "WHERE r.washmachine.machineId = :machineId " +
            "AND r.startTime = :startTime " +
            "AND r.date = :date " +
            "AND r.status = 'active'")
    boolean isSlotTaken(@Param("machineId") Integer machineId,
                        @Param("startTime") String startTime,
                        @Param("date") LocalDate date);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.washmachine.machineId = :machineId " +
            "AND r.date = :date " +
            "AND r.status = 'active'")
    List<Reservation> findByMachineIdAndDate(@Param("machineId") Integer machineId,
                                             @Param("date") LocalDate date);
}
