package com.start.waschmachine.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public Student register(@RequestBody Student student) {
        return studentService.registerStudent(student);
    }

    @PostMapping("/login")
    public ResponseEntity<Student> login(@RequestBody LoginRequest request) {
        Student student = studentService.login(request.getEmail(), request.getPassword());
        if (student == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(student);
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
