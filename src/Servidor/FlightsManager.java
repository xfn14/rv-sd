package Servidor;

import Utils.IFlightsManager;
import Utils.Tuple;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class FlightsManager implements IFlightsManager {
    private static int REFERENCE_NUMBER = 0;
    private final Map<String, Map<String, Flight>> flights;
    private final Map<String, Tuple<List<DailyBooking>, String>> codes;
    private final List<Boolean> availability;
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    ReentrantLock codesLock = new ReentrantLock();

    public FlightsManager() {
        this.flights = new HashMap<>();
        this.codes = new HashMap<>();
        this.availability = new ArrayList<>(Collections.nCopies(Flight.MAXDAYS, true));
    }

    public List<Tuple<String, String>> getScales(String origin, String destination) {
        this.readWriteLock.readLock().lock();
        List<Tuple<String, String>> list = new ArrayList<>();
        try {
            Map<String, Flight> firstMap = this.flights.get(origin);

            if (firstMap == null) return list;

            for (Map.Entry<String, Flight> entry : firstMap.entrySet()) {
                if (entry.getKey().equals(destination))
                    list.add(new Tuple<>("", ""));
                else {

                    Map<String, Flight> sndMap = this.flights.get(entry.getKey());

                    if (sndMap != null) {
                        for (Map.Entry<String, Flight> sndEntry : sndMap.entrySet()) {
                            if (sndEntry.getKey().equals(destination))
                                list.add(new Tuple<>(entry.getKey(), ""));
                            else {
                                Map<String, Flight> thrMap = this.flights.get(sndEntry.getKey());
                                if (thrMap != null && thrMap.containsKey(destination))
                                    list.add(new Tuple<>(entry.getKey(),sndEntry.getKey()));
                            }
                        }
                    }
                }
            }
        } finally {
            this.readWriteLock.readLock().unlock();
        }
        return list;

    }

    public List<Tuple<String, String>> getFlights() {
        this.readWriteLock.readLock().lock();
        try {
            return flights.values().stream()
                    .flatMap(x -> x.values().stream())
                    .map(y -> new Tuple<>(y.getOrigin(), y.getDestination()))
                    .collect(Collectors.toList());
        } finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    public boolean insertFlight(String username, String origin, String destination, int maxCapacity) {
        this.readWriteLock.writeLock().lock();
        try {
            Flight flight = new Flight(origin, destination, maxCapacity);

            Map<String, Flight> originFlights = this.flights.computeIfAbsent(origin, k -> new HashMap<>());

            if (originFlights.get(destination) != null)
                return false;

            originFlights.put(destination, flight);

            return true;
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    public String bookFlight(String username, List<String> journeys, int begin, int end) {
        boolean booked = false;
        int day = begin;
        String code = "";

        if (journeys.size() < 2 || begin < 0 || begin > end || end >= Flight.MAXDAYS)
            return code;

        List<DailyBooking> bookings = null;
        this.readWriteLock.readLock().lock();
        try {
            Set<Flight> flights = new TreeSet<>(Comparator.comparing(Flight::getOrigin)
                    .thenComparing(Flight::getDestination));

            for (int i = 0; i < journeys.size() - 1; ++i) {
                Map<String, Flight> map = this.flights.get(journeys.get(i));
                if (map == null) return code;
                Flight f = map.get(journeys.get(i + 1));
                if (f == null) return code;
                flights.add(f);
            }

            for (; day <= end && !booked; ++day) {
                if (this.availability.get(day)) {
                    bookings = new ArrayList<>();
                    boolean available = true;
                    for (Flight flight : flights) {
                        DailyBooking book = flight.getDailyBooking(day);
                        bookings.add(book);
                        book.lock.lock();
                        if (!book.checkAvailabity()) {
                            available = false;
                            break;
                        }
                    }
                    if (available) {
                        for (DailyBooking booking : bookings)
                            booking.addBooking();
                        booked = true;
                    }
                    for (DailyBooking booking : bookings)
                        booking.lock.unlock();
                }
            }
        } finally {
            this.readWriteLock.readLock().unlock();
            if (booked) {
                this.codesLock.lock();
                code = String.valueOf(REFERENCE_NUMBER++);
                this.codes.put(code, new Tuple<>(bookings, username));
                System.out.println("Book: " + code + " | Username: " + username + " | Day: " + (day - 1));
                this.codesLock.unlock();
            }
        }
        return code;
    }

    public boolean cancelBooking(String username, String code) {
        this.codesLock.lock();
        try {
            Tuple<List<DailyBooking>, String> tup = this.codes.get(code);
            System.out.println("Cancel " + code);

            if (tup == null || !tup.getY().equals(username))
                return false;

            this.readWriteLock.readLock().lock();
            try {
                if (!this.availability.get(tup.getX().get(0).getDay()))
                    return false;

                for (DailyBooking booking : tup.getX()) {
                    booking.lock.lock();
                    booking.removeBooking();
                    booking.lock.unlock();
                }

                this.codes.remove(code);
            } finally {
                this.readWriteLock.readLock().unlock();
            }
        } finally {
            this.codesLock.unlock();
        }
        return true;
    }

    public void cancelDay(String username, int day) {
        this.readWriteLock.writeLock().lock();
        try {
            this.availability.set(day, false);
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }
}
