package com.start.waschmachine.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

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
        return studentRepository.findById(id).orElse(null);
    }

    public Student loadBalance(int id, double amountEuros) {
        if (amountEuros < 5) {
            throw new IllegalArgumentException("Minimum load amount is €5");
        }
        Student student = studentRepository.findById(id).orElseThrow();
        student.setBalance(student.getBalance() + amountEuros);
        return studentRepository.save(student);
    }
}
