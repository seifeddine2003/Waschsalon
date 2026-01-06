package com.start.waschmachine.Reservation;

import com.start.waschmachine.Slot.Slot;
import com.start.waschmachine.Student.Student;
import com.start.waschmachine.Washmachine.Washmachine;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "machineId", nullable = false)
    private Washmachine washmachine;

    @ManyToOne
    @JoinColumn(name = "slotId", nullable = false)
    private Slot slot;

    protected Reservation() {}

    public Reservation(Student student, Washmachine washmachine, Slot slot, LocalDate date) {
        this.student = student;
        this.washmachine = washmachine;
        this.slot = slot;
        this.date = date;
        this.status = "active";
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public Student getStudent() {
        return student;
    }

    public Washmachine getWashmachine() {
        return washmachine;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
