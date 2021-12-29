package Servidor;

import java.util.*;
import java.util.stream.Collectors;

public class FlightsManager {
    private final Map<String, Map<String, List<Flight>>> flights;

    public FlightsManager() {
        this.flights = new HashMap<>();
    }



    public List<Flight> getFlights() {
        return flights.values().stream()
                .flatMap(x -> x.values().stream())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public void insertFlight(String origin, String destination, int maxCapacity) {
        Flight flight = new Flight(origin, destination, maxCapacity);

        Map<String, List<Flight>> originFlights = flights.computeIfAbsent(origin, k -> new HashMap<>());

        List<Flight> destinationFlights = originFlights.computeIfAbsent(destination, k -> new ArrayList<>());

        destinationFlights.add(flight);
    }
}
