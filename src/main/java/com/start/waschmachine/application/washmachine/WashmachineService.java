package com.start.waschmachine.application.washmachine;

import com.start.waschmachine.domain.washmachine.Washmachine;
import com.start.waschmachine.domain.washmachine.WashmachineRepository;
import com.start.waschmachine.exception.WashmachineNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WashmachineService implements IWashmachineService {

    @Autowired
    private WashmachineRepository repo;

    public List<Washmachine> getAllMachines() {
        return repo.findAll();
    }

    public Washmachine getById(int id) {
        return repo.findById(id).orElseThrow(() -> new WashmachineNotFoundException(id));
    }
}
