package com.start.waschmachine.Student;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    // ===== TEST 1: register a student =====
    @Test
    void registerStudent_success() {
        Student student = new Student("password123", "john@email.com", "Doe", "John");

        when(studentRepository.save(student)).thenReturn(student);

        Student result = studentService.registerStudent(student);

        assertNotNull(result);
        assertEquals("John", result.getVorname());
        assertEquals("john@email.com", result.getEmail());
        verify(studentRepository, times(1)).save(student);
    }

    // ===== TEST 2: login with correct credentials =====
    @Test
    void login_correctCredentials_returnsStudent() {
        Student student = new Student("password123", "john@email.com", "Doe", "John");

        when(studentRepository.findByEmail("john@email.com")).thenReturn(Optional.of(student));

        Student result = studentService.login("john@email.com", "password123");

        assertNotNull(result);
        assertEquals("john@email.com", result.getEmail());
    }

    // ===== TEST 3: login with wrong password =====
    @Test
    void login_wrongPassword_returnsNull() {
        Student student = new Student("password123", "john@email.com", "Doe", "John");

        when(studentRepository.findByEmail("john@email.com")).thenReturn(Optional.of(student));

        Student result = studentService.login("john@email.com", "wrongpassword");

        assertNull(result);
    }

    // ===== TEST 4: login with unknown email =====
    @Test
    void login_unknownEmail_returnsNull() {
        when(studentRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        Student result = studentService.login("unknown@email.com", "password123");

        assertNull(result);
    }

    // ===== TEST 5: get student by id — found =====
    @Test
    void getStudent_found_returnsStudent() {
        Student student = new Student("password123", "john@email.com", "Doe", "John");

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        Student result = studentService.getStudent(1);

        assertNotNull(result);
        assertEquals("Doe", result.getNachname());
    }

    // ===== TEST 6: get student by id — not found =====
    @Test
    void getStudent_notFound_returnsNull() {
        when(studentRepository.findById(99)).thenReturn(Optional.empty());

        Student result = studentService.getStudent(99);

        assertNull(result);
    }
}