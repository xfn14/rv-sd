package Servidor;

public class Flight {
    private final String origin;
    private final String destination;
    private final int capacity;

    public Flight(String origin, String destination, int capacity) {
        this.origin = origin;
        this.destination = destination;
        this.capacity = capacity;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Flight)) {
            return false;
        }


        Flight flight = (Flight) o;

        return flight.getOrigin().equals(origin)
                && flight.getDestination().equals(destination)
                && flight.getCapacity() == capacity;
    }
}
