package com.start.waschmachine.infrastructure.web;

import com.start.waschmachine.domain.student.Student;

public class AuthResponse {
    private String token;
    private int studentId;
    private String vorname;
    private String nachname;
    private String email;
    private double balance;

    public AuthResponse(String token, Student student) {
        this.token = token;
        this.studentId = student.getStudentId();
        this.vorname = student.getVorname();
        this.nachname = student.getNachname();
        this.email = student.getEmail();
        this.balance = student.getBalance();
    }

    public String getToken()    { return token; }
    public int getStudentId()   { return studentId; }
    public String getVorname()  { return vorname; }
    public String getNachname() { return nachname; }
    public String getEmail()    { return email; }
    public double getBalance()  { return balance; }
}
