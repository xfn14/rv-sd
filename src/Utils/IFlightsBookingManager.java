package Utils;

import Servidor.Flight;

import java.util.List;

public interface IFlightsBookingManager {
    String bookFlight(String username, String origin, String destination, int begin, int end);
    String bookFlight(String username, List<String> journey, int begin, int end);

    boolean cancelFlight(String username, String code);

    List<Flight> getFlights();

    void insertFlight(String origin, String destination, int capacity);
}
