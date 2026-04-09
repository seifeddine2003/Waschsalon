package com.start.waschmachine.exception;

public class StudentNotFoundException extends NotFoundException {
    public StudentNotFoundException(int id) {
        super("Student not found (id=" + id + ")");
    }
}
