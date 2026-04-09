package com.start.waschmachine.domain.reservation;

import com.start.waschmachine.domain.student.Student;
import com.start.waschmachine.domain.washmachine.Washmachine;
import com.start.waschmachine.exception.ReservationConflictException;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
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

    @Column
    private Integer washDuration;

    @Column
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "machineId", nullable = false)
    private Washmachine washmachine;

    protected Reservation() {}

    public Reservation(Student student, Washmachine washmachine,
                       String startTime, String endTime, LocalDate date) {
        this.student = student;
        this.washmachine = washmachine;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.status = "active";
    }

    public Integer getReservationId()       { return reservationId; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public LocalDate getDate()              { return date; }
    public String getStatus()               { return status; }
    public String getStartTime()            { return startTime; }
    public String getEndTime()              { return endTime; }
    public String getWashType()             { return washType; }
    public Integer getWashDuration()        { return washDuration; }
    public BigDecimal getPrice()            { return price; }
    public Student getStudent()             { return student; }
    public Washmachine getWashmachine()     { return washmachine; }

    public void cancel() {
        if (!"active".equals(this.status)) throw new ReservationConflictException("Reservation is already cancelled");
        this.status = "cancelled";
    }

    public void setDate(LocalDate date)         { this.date = date; }
    public void setStatus(String status)        { this.status = status; }
    public void setStartTime(String s)          { this.startTime = s; }
    public void setEndTime(String s)            { this.endTime = s; }
    public void setWashType(String s)           { this.washType = s; }
    public void setWashDuration(Integer d)      { this.washDuration = d; }
    public void setPrice(BigDecimal p)          { this.price = p; }
}
