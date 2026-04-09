package com.start.waschmachine.application.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservationRequest {

    @NotNull
    private Integer studentId;
    @NotNull
    private Integer machineId;
    @NotBlank
    private String startTime;
    @NotBlank
    private String endTime;
    @NotNull
    private LocalDate date;
    @NotBlank
    private String washType;
    @NotNull @Positive
    private Integer washDuration;
    @NotNull @Positive
    private BigDecimal price;

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getMachineId() { return machineId; }
    public void setMachineId(Integer machineId) { this.machineId = machineId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getWashType() { return washType; }
    public void setWashType(String washType) { this.washType = washType; }

    public Integer getWashDuration() { return washDuration; }
    public void setWashDuration(Integer washDuration) { this.washDuration = washDuration; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
