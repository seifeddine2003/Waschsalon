package com.start.waschmachine.application.student;

import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.domain.student.StudentRepository;
import com.start.waschmachine.exception.StudentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentService implements IStudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Student registerStudent(Student s) {
        s.setPassword(passwordEncoder.encode(s.getPassword()));
        Student saved = studentRepository.save(s);
        log.info("New student registered: {} (id={})", saved.getEmail(), saved.getStudentId());
        return saved;
    }

    public Student login(String email, String password) {
        return studentRepository.findByEmail(email)
                .filter(s -> {
                    boolean match = passwordEncoder.matches(password, s.getPassword());
                    if (!match) log.warn("Failed login attempt for email: {}", email);
                    return match;
                })
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
        Student saved = studentRepository.save(student);
        log.info("Balance loaded: student={}, amount=€{}, newBalance=€{}", id, amount, saved.getBalance());
        return saved;
    }

    public Student refundBalance(int id, BigDecimal amount) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        student.refundBalance(amount);
        Student saved = studentRepository.save(student);
        log.info("Balance refunded: student={}, amount=€{}, newBalance=€{}", id, amount, saved.getBalance());
        return saved;
    }

    public Student deductBalance(int id, BigDecimal amount) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        student.deductBalance(amount);
        Student saved = studentRepository.save(student);
        log.info("Balance deducted: student={}, amount=€{}, newBalance=€{}", id, amount, saved.getBalance());
        return saved;
    }
}
