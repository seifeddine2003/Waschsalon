package com.start.waschmachine.infrastructure.web;

import com.start.waschmachine.application.student.IStudentService;
import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private IStudentService studentService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Student register(@Valid @RequestBody Student student) {
        return studentService.registerStudent(student);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Student student = studentService.login(request.getEmail(), request.getPassword());
        if (student == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String token = jwtUtil.generateToken(student.getEmail(), student.getStudentId(), student.getRole());
        return ResponseEntity.ok(new AuthResponse(token, student));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public Student getStudent(@PathVariable int id) {
        return studentService.getStudent(id);
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable int id) {
        Student student = studentService.getStudent(id);
        return ResponseEntity.ok(Map.of("balance", student.getBalance()));
    }

    @PostMapping("/{id}/balance/load")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Student> loadBalance(@PathVariable int id, @Valid @RequestBody LoadBalanceRequest request) {
        return ResponseEntity.ok(studentService.loadBalance(id, request.getAmount()));
    }
}
