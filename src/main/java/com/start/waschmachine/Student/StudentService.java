package com.start.waschmachine.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student registerStudent(Student s) {
        return studentRepository.save(s);
    }
    public Student login(String email, String password) {
        return studentRepository.findByEmail(email)
                .filter(s -> s.getPassword().equals(password))
                .orElse(null);
    }

    public Student getStudent(int id) {
        return studentRepository.findById(id).orElse(null);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
//The service performs the important internal operations:
//
//Saving data
//
//Validating data
//
//Checking if email already exists
//
//Calculating balances
//
//Applying rules
//
//Talking to the repository to access the database
