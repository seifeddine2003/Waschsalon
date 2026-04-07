package com.start.waschmachine.Washmachine;

import com.start.waschmachine.application.washmachine.IWashmachineService;
import com.start.waschmachine.domain.washmachine.Washmachine;
import com.start.waschmachine.infrastructure.security.JwtUtil;
import com.start.waschmachine.infrastructure.web.WashmachineController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WashmachineController.class)
@WithMockUser
class WashmachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IWashmachineService washService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAll_returnsMachines() throws Exception {
        Washmachine m1 = new Washmachine("M1", "Available", null, null, true);
        Washmachine m2 = new Washmachine("M2", "In Use", 20, "John", false);

        when(washService.getAllMachines()).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/washmachines/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAll_machinesHaveRequiredFields() throws Exception {
        Washmachine m1 = new Washmachine("M1", "Available", null, null, true);

        when(washService.getAllMachines()).thenReturn(List.of(m1));

        mockMvc.perform(get("/washmachines/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("M1"))
                .andExpect(jsonPath("$[0].status").value("Available"))
                .andExpect(jsonPath("$[0].isOpen").value(true));
    }
}
