package com.start.waschmachine.application.student;

import com.start.waschmachine.domain.student.Student;

import java.math.BigDecimal;

public interface IStudentService {
    Student registerStudent(Student s);
    Student login(String email, String password);
    Student getStudent(int id);
    Student loadBalance(int id, BigDecimal amount);
    Student deductBalance(int id, BigDecimal amount);
    Student refundBalance(int id, BigDecimal amount);
}
