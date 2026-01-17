package com.start.waschmachine.Washmachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/washmachines")
public class WashmachineController {

    @Autowired
    private WashmachineService washService;

    @GetMapping("/all")
    public List<Map<String, Object>> getAll() {
        return washService.getAllMachines().stream()
                .map(w -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", w.getMachineId());
                    map.put("name", w.getMachineNr());
                    map.put("status", w.getStatus());
                    map.put("timeRemaining", w.getTimeRemaining());
                    map.put("users", w.getUsers() != null ? w.getUsers().split(",") : null);
                    map.put("isOpen", w.getIsOpen());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
