package com.start.waschmachine.Student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`studentId`" , unique = true)
    private int studentId;

    @NotBlank(message = "First name is required")
    private String vorname;

    @NotBlank(message = "Last name is required")
    private String nachname;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private double balance = 0.0;

    public Student() {};
    public Student(String password, String email, String nachname, String vorname) {
        this.password = password;
        this.email = email;
        this.nachname = nachname;
        this.vorname = vorname;
    }
    public void setStudentId(int id) {
        this.studentId = id;
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

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getStudentId() {
        return studentId;
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

    public double getBalance() {
        return balance;
    }
}
