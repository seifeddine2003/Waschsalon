package com.start.waschmachine.exception;

public class ReservationNotFoundException extends NotFoundException {
    public ReservationNotFoundException(int id) {
        super("Reservation not found (id=" + id + ")");
    }
}
