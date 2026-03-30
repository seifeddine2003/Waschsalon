package com.start.waschmachine.Washmachine;

import jakarta.persistence.*;

@Entity
@Table(name = "Washmachine")
public class Washmachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machineId")
    private Integer machineId;

    @Column(name = "machineNr", nullable = false)
    private String machineNr;

    @Column(name = "status")
    private String status;

    @Column(name = "timeRemaining")
    private Integer timeRemaining;

    @Column(name = "users")
    private String users;

    @Column(name = "isOpen")
    private Boolean isOpen;

    protected Washmachine() {}

    public Washmachine(String machineNr, String status, Integer timeRemaining, String users, Boolean isOpen) {
        this.machineNr = machineNr;
        this.status = status;
        this.timeRemaining = timeRemaining;
        this.users = users;
        this.isOpen = isOpen;
    }

    public Integer getMachineId() { return machineId; }
    public String getMachineNr() { return machineNr; }
    public String getStatus() { return status; }
    public Integer getTimeRemaining() { return timeRemaining; }
    public String getUsers() { return users; }
    public Boolean getIsOpen() { return isOpen; }

    public void setMachineNr(String machineNr) { this.machineNr = machineNr; }
    public void setStatus(String status) { this.status = status; }
    public void setTimeRemaining(Integer timeRemaining) { this.timeRemaining = timeRemaining; }
    public void setUsers(String users) { this.users = users; }
    public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }
}
