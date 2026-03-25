package com.start.waschmachine.Reservation;

import com.start.waschmachine.Student.Student;
import com.start.waschmachine.Washmachine.Washmachine;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String status = "active";

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    @Column
    private String washType;

    @Column(nullable = false)
    private Integer washDuration;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "machineId", nullable = false)
    private Washmachine washmachine;

    protected Reservation() {}

    public Reservation(Student student, Washmachine washmachine, String startTime, String endTime, LocalDate date) {
        this.student = student;
        this.washmachine = washmachine;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.status = "active";
    }

    public Integer getReservationId() { return reservationId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getWashType() { return washType; }
    public Integer getWashDuration() { return washDuration; }
    public Student getStudent() { return student; }
    public Washmachine getWashmachine() { return washmachine; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setWashType(String washType) { this.washType = washType; }
    public void setWashDuration(Integer washDuration) { this.washDuration = washDuration; }
}