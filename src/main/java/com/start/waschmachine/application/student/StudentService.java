package com.start.waschmachine.application.student;

import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.domain.student.StudentRepository;
import com.start.waschmachine.exception.StudentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentService implements IStudentService {

    @Autowired
    private StudentRepository studentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Student registerStudent(Student s) {
        s.setPassword(passwordEncoder.encode(s.getPassword()));
        return studentRepository.save(s);
    }

    public Student login(String email, String password) {
        return studentRepository.findByEmail(email)
                .filter(s -> passwordEncoder.matches(password, s.getPassword()))
                .orElse(null);
    }

    public Student getStudent(int id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student loadBalance(int id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(5)) < 0) {
            throw new IllegalArgumentException("Minimum load amount is €5");
        }
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        student.refundBalance(amount);
        return studentRepository.save(student);
    }

    public Student refundBalance(int id, BigDecimal amount) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        student.refundBalance(amount);
        return studentRepository.save(student);
    }

    public Student deductBalance(int id, BigDecimal amount) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        student.deductBalance(amount);
        return studentRepository.save(student);
    }
}
