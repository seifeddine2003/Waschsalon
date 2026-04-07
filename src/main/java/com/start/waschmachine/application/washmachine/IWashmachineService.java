package com.start.waschmachine.application.washmachine;

import com.start.waschmachine.domain.washmachine.Washmachine;

import java.util.List;

public interface IWashmachineService {
    List<Washmachine> getAllMachines();
    Washmachine getById(int id);
}
