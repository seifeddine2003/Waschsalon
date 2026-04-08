package com.start.waschmachine.Reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.waschmachine.application.reservation.IReservationService;
import com.start.waschmachine.application.reservation.ReservationRequest;
import com.start.waschmachine.domain.reservation.Reservation;
import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.domain.washmachine.Washmachine;
import com.start.waschmachine.infrastructure.security.JwtUtil;
import com.start.waschmachine.infrastructure.web.ReservationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@WithMockUser
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IReservationService reservationService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createReservation_success_returns200() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("startTime", "10:00");
        mockResult.put("endTime", "10:30");
        mockResult.put("washType", "Quick Wash");
        mockResult.put("newBalance", 0.0);

        when(reservationService.createReservation(any())).thenReturn(mockResult);

        ReservationRequest req = new ReservationRequest();
        req.setStudentId(1);
        req.setMachineId(1);
        req.setStartTime("10:00");
        req.setEndTime("10:30");
        req.setDate(LocalDate.now());
        req.setWashType("Quick Wash");
        req.setWashDuration(30);

        mockMvc.perform(post("/reservations/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime").value("10:00"))
                .andExpect(jsonPath("$.washType").value("Quick Wash"));
    }

    @Test
    void createReservation_conflict_returns409() throws Exception {
        when(reservationService.createReservation(any()))
                .thenThrow(new RuntimeException("This slot is already reserved for this machine"));

        ReservationRequest req = new ReservationRequest();
        req.setStudentId(1);
        req.setMachineId(1);
        req.setStartTime("10:00");
        req.setEndTime("10:30");
        req.setDate(LocalDate.now());
        req.setWashType("Quick Wash");
        req.setWashDuration(30);

        mockMvc.perform(post("/reservations/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("This slot is already reserved for this machine"));
    }

    @Test
    void getAll_returnsList() throws Exception {
        Student student = new Student("password123", "john@email.com", "Doe", "John");
        Washmachine machine = new Washmachine("M1", "Available", null, null, true);

        Reservation r1 = new Reservation(student, machine, "10:00", "10:30", LocalDate.now());
        Reservation r2 = new Reservation(student, machine, "11:00", "11:30", LocalDate.now());

        when(reservationService.getAll()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/reservations/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
