package com.start.waschmachine.Reservation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotControllerTest {

    @Mock
    private ReservationRepository reservationRepo;

    @InjectMocks
    private SlotController slotController;

    @Test
    void getAvailableSlots_noReservations_returnsAllFutureSlots() {
        when(reservationRepo.findByMachineIdAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(List.of());

        List<Map<String, Object>> slots = slotController.getAvailableSlots(1);

        assertNotNull(slots);
        assertFalse(slots.isEmpty());

        for (Map<String, Object> slot : slots) {
            assertTrue(slot.containsKey("startTime"));
            assertTrue(slot.containsKey("endTime"));
        }
    }

    @Test
    void getAvailableSlots_quickWash_blocksTwoSlots() {
        Reservation r = mock(Reservation.class);
        when(r.getStartTime()).thenReturn("06:00");
        when(r.getWashDuration()).thenReturn(30);

        when(reservationRepo.findByMachineIdAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(List.of(r));

        List<Map<String, Object>> slots = slotController.getAvailableSlots(1);

        boolean hasBlockedSlot = slots.stream()
                .anyMatch(s -> s.get("startTime").equals("06:00")
                        || s.get("startTime").equals("06:15")
                        || s.get("startTime").equals("06:30"));

        assertFalse(hasBlockedSlot, "Slots covered by the reservation should be blocked");
    }

    @Test
    void getAvailableSlots_slotsAreInOrder() {
        when(reservationRepo.findByMachineIdAndDate(eq(1), any(LocalDate.class)))
                .thenReturn(List.of());

        List<Map<String, Object>> slots = slotController.getAvailableSlots(1);

        for (int i = 0; i < slots.size() - 1; i++) {
            String current = (String) slots.get(i).get("startTime");
            String next    = (String) slots.get(i + 1).get("startTime");
            assertTrue(current.compareTo(next) < 0,
                       "Slots should be in chronological order");
        }
    }
}