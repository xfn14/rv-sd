package tests;

import Cliente.StubAccountManager;
import Cliente.StubFlightsManager;
import Utils.Connection;
import Utils.IAccountsManager;
import Utils.IFlightsManager;
import Utils.Tuple;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class BotClient {
    private final Socket socket;
    private final Connection connection;
    private final IAccountsManager accountsManager;
    private final IFlightsManager flightsManager;
    private String username;

    public BotClient(String username) throws IOException {
        this.socket = new Socket("localhost", 12345);
        this.connection = new Connection(this.socket);
        this.accountsManager = new StubAccountManager(this.connection);
        this.flightsManager = new StubFlightsManager(this.connection);

        if (username.equals("admin")) {
            this.login(username, username);
            this.username = username;
        } else this.registerAccount("BOT" + username, "bot");
    }

    public void cancelDay(int day) {
        this.flightsManager.cancelDay(this.username, day);
    }

    public boolean insertFlight(String origin, String destination, int max) {
        return this.flightsManager.insertFlight(this.username, origin, destination, max);
    }

    public String bookFlight(List<String> journey, int begin, int end) {
        return this.flightsManager.bookFlight(this.username, journey, begin, end);
    }

    public void cancelFlight(String code) {
        this.flightsManager.cancelBooking(this.username, code);
    }

    public List<Tuple<String, String>> getFlights() {
        return this.flightsManager.getFlights();
    }

    public void registerAccount(String username, String password) {
        this.accountsManager.createAccount(username, password);
        this.username = username;
    }

    public int login(String username, String password) {
        return this.accountsManager.login(username, password);
    }

    public void close() throws IOException {
        this.connection.close();
        this.socket.close();
    }
}
