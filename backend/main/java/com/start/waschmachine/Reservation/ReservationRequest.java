package com.start.waschmachine.Reservation;

import java.time.LocalDate;

public class ReservationRequest {

    private Integer studentId;
    private Integer machineId;
    private String startTime;
    private String endTime;
    private LocalDate date;
    private String washType;
    private Integer washDuration;

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
}