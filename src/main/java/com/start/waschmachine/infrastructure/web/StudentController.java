package com.start.waschmachine.infrastructure.web;

import com.start.waschmachine.application.student.IStudentService;
import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Student student = studentService.login(request.getEmail(), request.getPassword());
        if (student == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String token = jwtUtil.generateToken(student.getEmail(), student.getStudentId());
        return ResponseEntity.ok(new AuthResponse(token, student));
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable int id) {
        return studentService.getStudent(id);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable int id) {
        Student student = studentService.getStudent(id);
        if (student == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("balance", student.getBalance()));
    }

    @PostMapping("/{id}/balance/load")
    public ResponseEntity<?> loadBalance(@PathVariable int id, @RequestBody LoadBalanceRequest request) {
        try {
            Student student = studentService.loadBalance(id, request.getAmount());
            return ResponseEntity.ok(student);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
