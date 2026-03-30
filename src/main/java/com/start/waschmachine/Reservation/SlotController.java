package com.start.waschmachine.Reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/slots")
public class SlotController {

    @Autowired
    private ReservationRepository reservationRepo;

    @GetMapping("/available")
    public List<Map<String, Object>> getAvailableSlots(@RequestParam Integer machineId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        List<Reservation> reservations = reservationRepo
                .findByMachineIdAndDate(machineId, today);

        // Build a set of all blocked start times
        Set<String> blockedSlots = new HashSet<>();
        for (Reservation r : reservations) {
            LocalTime resStart = LocalTime.parse(r.getStartTime(), fmt);
            LocalTime resEnd   = resStart.plusMinutes(r.getWashDuration() + 15);

            LocalTime t = resStart;
            while (t.isBefore(resEnd)) {
                blockedSlots.add(t.format(fmt));
                t = t.plusMinutes(15);  // ← 15 min increments
            }
        }

        // Generate available slots
        List<Map<String, Object>> availableSlots = new ArrayList<>();
        LocalTime start = LocalTime.of(6, 0);
        LocalTime end   = LocalTime.of(23, 0);

        while (start.isBefore(end)) {
            LocalTime slotEnd = start.plusMinutes(15);
            String startStr   = start.format(fmt);
            String endStr     = slotEnd.format(fmt);

            if (start.isAfter(now) && !blockedSlots.contains(startStr)) {
                Map<String, Object> slot = new HashMap<>();
                slot.put("startTime", startStr);
                slot.put("endTime",   endStr);
                availableSlots.add(slot);
            }

            start = slotEnd;
        }

        return availableSlots;
    }
}