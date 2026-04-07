package com.start.waschmachine.Reservation;

import com.start.waschmachine.application.reservation.IReservationService;
import com.start.waschmachine.infrastructure.security.JwtUtil;
import com.start.waschmachine.infrastructure.web.SlotController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SlotController.class)
@WithMockUser
class SlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IReservationService reservationService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAvailableSlots_delegatesToService() throws Exception {
        List<Map<String, Object>> slots = List.of(
                Map.of("startTime", "10:00", "endTime", "10:15"),
                Map.of("startTime", "10:15", "endTime", "10:30")
        );
        when(reservationService.getAvailableSlots(1)).thenReturn(slots);

        mockMvc.perform(get("/slots/available").param("machineId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].startTime").value("10:00"));

        verify(reservationService, times(1)).getAvailableSlots(1);
    }
}
