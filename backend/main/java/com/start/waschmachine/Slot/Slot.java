package com.start.waschmachine.Slot;

import jakarta.persistence.*;

@Entity
@Table(name = "slot")
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slotId")
    private int slotId;
    @Column(name = "startTime")
    private String startTime;
    @Column(name = "endTime")
    private String endTime;

    public Slot() {}

    public Slot(int slotId, String startTime, String endTime) {
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getSlotId() {
        return slotId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setSlotId(int id) {
        this.slotId = id;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
