package com.start.waschmachine.Reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.start.waschmachine.application.reservation.ReservationRequest;
import com.start.waschmachine.domain.reservation.ReservationRepository;
import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.domain.student.StudentRepository;
import com.start.waschmachine.domain.washmachine.Washmachine;
import com.start.waschmachine.domain.washmachine.WashmachineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@WithMockUser
public class ReservationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WashmachineRepository washmachineRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private ObjectMapper objectMapper;
    private Washmachine machine;
    private Student student;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        machine = washmachineRepository.save(
                new Washmachine("M-01", "active", null, null, true)
        );
        student = studentRepository.save(
                new Student("pass1234", "john@example.com", "Doe", "John")
        );
    }

    private ReservationRequest buildRequest(String startTime, String endTime, LocalDate date) {
        ReservationRequest req = new ReservationRequest();
        req.setStudentId(student.getStudentId());
        req.setMachineId(machine.getMachineId());
        req.setStartTime(startTime);
        req.setEndTime(endTime);
        req.setDate(date);
        req.setWashType("cotton");
        req.setWashDuration(45);
        return req;
    }

    @Test
    void createReservation_shouldReturn200_whenSlotIsFree() throws Exception {
        ReservationRequest req = buildRequest("10:00", "10:45", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime").value("10:00"))
                .andExpect(jsonPath("$.washType").value("cotton"));
    }

    @Test
    void createReservation_shouldReturn409_whenSlotIsAlreadyTaken() throws Exception {
        ReservationRequest req = buildRequest("10:00", "10:45", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("already reserved")));
    }

    @Test
    void createReservation_shouldReturn409_whenStudentNotFound() throws Exception {
        ReservationRequest req = buildRequest("11:00", "11:45", LocalDate.now().plusDays(1));
        req.setStudentId(99999);

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Student not found")));
    }

    @Test
    void createReservation_shouldReturn409_whenMachineNotFound() throws Exception {
        ReservationRequest req = buildRequest("11:00", "11:45", LocalDate.now().plusDays(1));
        req.setMachineId(99999);

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Washmachine not found")));
    }

    @Test
    void createReservation_shouldAllowSameTime_onDifferentMachine() throws Exception {
        Washmachine otherMachine = washmachineRepository.save(
                new Washmachine("M-02", "active", null, null, true)
        );

        ReservationRequest req1 = buildRequest("10:00", "10:45", LocalDate.now().plusDays(1));
        ReservationRequest req2 = buildRequest("10:00", "10:45", LocalDate.now().plusDays(1));
        req2.setMachineId(otherMachine.getMachineId());

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isOk());
    }

    @Test
    void createReservation_shouldAllowSameTime_onDifferentDate() throws Exception {
        ReservationRequest req1 = buildRequest("10:00", "10:45", LocalDate.now().plusDays(1));
        ReservationRequest req2 = buildRequest("10:00", "10:45", LocalDate.now().plusDays(2));

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoReservations() throws Exception {
        mockMvc.perform(get("/reservations/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAll_shouldReturnAllReservations_afterCreating() throws Exception {
        ReservationRequest req = buildRequest("10:00", "10:45", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/reservations/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reservations/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].startTime").value("10:00"));
    }
}
