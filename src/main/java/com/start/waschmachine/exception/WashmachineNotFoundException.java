package com.start.waschmachine.exception;

public class WashmachineNotFoundException extends NotFoundException {
    public WashmachineNotFoundException(int id) {
        super("Washmachine not found (id=" + id + ")");
    }
}
