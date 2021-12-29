package Servidor;

import Utils.IFlightsBookingManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightsBookingManager extends FlightsManager implements IFlightsBookingManager {
    private final Map<String, FlightBooking> bookings;
    private final Map<LocalDate, Map<Flight, Integer>> flightsOccupancy;

    public FlightsBookingManager() {
        this.bookings = new HashMap<>();
        this.flightsOccupancy = new HashMap<>();
    }

    public String bookFlight(String username, String origin, String destination, LocalDate begin, LocalDate end) {
        return "";
    }
    public String bookFlight(String username, List<String> journey, int begin, int end) {
        return "";
    }

    public boolean cancelFlight(String username, String code) {
        FlightBooking booking = bookings.get(code);
        if (booking == null || !booking.getOwner().equals(username))
            return false;

        bookings.remove(code);

        flightsOccupancy.get(booking.getDate())
                .computeIfPresent(booking.getFlight(), (k, v) -> v - 1);

        return true;
    }
}
