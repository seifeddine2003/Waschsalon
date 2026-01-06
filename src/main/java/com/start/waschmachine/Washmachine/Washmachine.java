package com.start.waschmachine.Washmachine;

import jakarta.persistence.*;

@Entity
@Table(name = "washmachine")
public class Washmachine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machineId")
    private Integer machineId;

    @Column(name = "machineNr", nullable = false)
    private String machineNr;

    @Column(name = "status")
    private String statut;

    protected Washmachine() {
    }

    public Washmachine(String machineNr, String statut) {
        this.machineNr = machineNr;
        this.statut = statut;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public String getMachineNr() {
        return machineNr;
    }

    public String getStatut() {
        return statut;
    }

    public void setMachineNr(String machineNr) {
        this.machineNr = machineNr;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
