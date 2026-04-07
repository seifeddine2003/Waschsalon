package com.start.waschmachine.Student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.waschmachine.application.student.IStudentService;
import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.infrastructure.security.JwtUtil;
import com.start.waschmachine.infrastructure.web.StudentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@WithMockUser
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStudentService studentService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_returnsStudent() throws Exception {
        Student student = new Student("password123", "john@email.com", "Doe", "John");

        when(studentService.registerStudent(any())).thenReturn(student);

        String json = """
                {
                    "vorname": "John",
                    "nachname": "Doe",
                    "email": "john@email.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/students/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@email.com"))
                .andExpect(jsonPath("$.vorname").value("John"));
    }

    @Test
    void login_validCredentials_returns200() throws Exception {
        Student student = new Student("password123", "john@email.com", "Doe", "John");

        when(studentService.login("john@email.com", "password123")).thenReturn(student);

        String loginJson = """
                {
                    "email": "john@email.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/students/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@email.com"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(studentService.login("john@email.com", "wrongpassword")).thenReturn(null);

        String loginJson = """
                {
                    "email": "john@email.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/students/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getStudent_existingId_returnsStudent() throws Exception {
        Student student = new Student("password123", "john@email.com", "Doe", "John");

        when(studentService.getStudent(1)).thenReturn(student);

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nachname").value("Doe"));
    }

    @Test
    void getStudent_nonExistentId_returnsNull() throws Exception {
        when(studentService.getStudent(99)).thenReturn(null);

        mockMvc.perform(get("/students/99"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
