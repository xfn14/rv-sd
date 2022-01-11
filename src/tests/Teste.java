package tests;

import Servidor.Flight;
import Servidor.Server;
import Utils.RandomUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Teste {
    private Server server;
    private BotClient botClient;
    private List<Flight> flights;

    @BeforeAll
    void before() throws IOException {
        new Thread(() -> {
            this.server = new Server();
            try {
                this.server.initServer();
            } catch (IOException ignored) {
            }
        }).start();

        this.botClient = new BotClient("admin");
        this.flights = new ArrayList<>();
    }

    @Test
    @Order(1)
    void insertFlight() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Thread thread = new Thread(() -> {
                try {
                    BotClient bot = new BotClient("admin");
                    for (int j = 0; j < 100; j++) {
                        String origin = RandomUtils.randomString();
                        String destination = RandomUtils.randomString();
                        boolean status = bot.insertFlight(origin, destination, 20);
                        assertTrue(status);
                        this.flights.add(new Flight(origin, destination, 20));
                    }
                    bot.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    fail("Bot-admin connection failed");
                }
            });
            thread.start();
            threads.add(thread);
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @Order(2)
    void bookFlight(){
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Thread thread = new Thread(() -> {
                try {
                    BotClient bot = new BotClient(RandomUtils.randomString());
                    for (Flight flight : this.flights) {
                        String origin = flight.getOrigin(), destination = flight.getDestination();
                        String result = bot.bookFlight(List.of(origin, destination), 2, 10);
                        if (result.equals(""))
                            fail("Failed to book flight");
                    }
                    bot.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    fail("Failed to contact server");
                }
            });
            thread.start();
            threads.add(thread);
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterAll
    void after() throws IOException {
        this.server.stop();
        this.botClient.close();
    }
}
