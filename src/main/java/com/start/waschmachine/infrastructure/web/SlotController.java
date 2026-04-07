package com.start.waschmachine.infrastructure.web;

import com.start.waschmachine.application.reservation.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/slots")
public class SlotController {

    @Autowired
    private IReservationService reservationService;

    @GetMapping("/available")
    public List<Map<String, Object>> getAvailableSlots(@RequestParam Integer machineId) {
        return reservationService.getAvailableSlots(machineId);
    }
}
