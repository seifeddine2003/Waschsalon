package com.start.waschmachine.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByEmail(String email);
}

//This allows Spring to handle:
//
//Saving a Student
//
//Finding a Student by ID
//
//Deleting
//
//Updating
//
//Checking email uniqueness