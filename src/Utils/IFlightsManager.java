package Utils;

import java.util.List;

public interface IFlightsManager {
    List<Tuple<String, String>> getFlights();

    boolean insertFlight(String username, String origin, String destination, int maxCapacity);

    String bookFlight(String username, List<String> journeys, int begin, int end);

    boolean cancelBooking(String username, String code);

    void cancelDay(String username, int day);
}
