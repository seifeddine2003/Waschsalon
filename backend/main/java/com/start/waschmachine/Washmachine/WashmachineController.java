package com.start.waschmachine.Washmachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/washmachines")

public class WashmachineController {

    @Autowired
    private WashmachineService washService;

    @GetMapping("/all")
    public List<Washmachine> getAll() {
        return washService.getAllMachines();
    }
}

