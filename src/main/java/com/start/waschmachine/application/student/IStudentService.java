package com.start.waschmachine.application.student;

import com.start.waschmachine.domain.student.Student;

public interface IStudentService {
    Student registerStudent(Student s);
    Student login(String email, String password);
    Student getStudent(int id);
    Student loadBalance(int id, double amountEuros);
    Student deductBalance(int id, double amount);
}
