package Servidor;

import java.util.ArrayList;
import java.util.List;


public class Flight {
    private final String origin;
    private final String destination;
    private final int capacity;
    public static final int MAXDAYS = 60;
    private final List<DailyBooking> bookings;

    public Flight(String origin, String destination, int capacity) {
        this.origin = origin;
        this.destination = destination;
        this.capacity = capacity;
        this.bookings = new ArrayList<>(MAXDAYS);
        for(int i = 0; i < MAXDAYS; ++i) {
            this.bookings.add(new DailyBooking(i, capacity));
        }
    }

    public String getDestination() {
        return destination;
    }

    public String getOrigin() {
        return origin;
    }

    public DailyBooking getDailyBooking(int day){
        return this.bookings.get(day);
    }


}
