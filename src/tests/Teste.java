package tests;

import Servidor.Server;
import Utils.RandomUtils;
import Utils.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Teste {
    private Server server;
    private BotClient botClient;

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
    }

    @Test
    void insertFlight() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Thread thread = new Thread(() -> {
                try {
                    BotClient bot = new BotClient("admin");
                    for (int j = 0; j < 500; j++) {
                        bot.insertFlight(
                                RandomUtils.randomString(),
                                RandomUtils.randomString(),
                                RandomUtils.randomInt(1, 20)
                        );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
        List<Tuple<String, String>> voos = this.botClient.getFlights();
        System.out.println("\t\t****** Lista de voos disponíveis ******");
        for (Tuple<String, String> tup : voos) {
            System.out.println("Origem -> " + tup.getX() + "  Destino -> " + tup.getY());
        }
        assertEquals(20 * 500, voos.size());
    }

    @AfterAll
    void after() {
        this.server.stop();
    }
}
