package com.start.waschmachine.Student;

import jakarta.persistence.*;

@Entity
@Table(name = "Student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`studentId`" , unique = true)
    private int StudentId;

    private String vorname;
    private String nachname;
    private String email;
    private String password;
    private int balance;

    public Student() {};
    public Student(int balance, String password, String email, String nachname, String vorname, int studentId) {
        this.balance = balance;
        this.password = password;
        this.email = email;
        this.nachname = nachname;
        this.vorname = vorname;
        this.StudentId = studentId;
    }
    public void setStudentId(int id) {
        this.StudentId = id;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getStudentId() {
        return StudentId;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getBalance() {
        return balance;
    }
}
