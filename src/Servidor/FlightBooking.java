package Servidor;

import java.time.LocalDate;

public class FlightBooking {
    private final String owner;
    private final Flight flight;
    private final LocalDate date;

    public FlightBooking(String owner, Flight flight, LocalDate date) {
        this.owner = owner;
        this.flight = flight;
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public Flight getFlight() {
        return flight.clone();
    }

    public LocalDate getDate() {
        return date;
    }



}
