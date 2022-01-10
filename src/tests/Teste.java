package tests;

import Servidor.Server;
import Utils.RandomUtils;
import Utils.Tuple;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

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
        boolean b = this.botClient.insertFlight("Porto", "Lisboa", 10);
        assertTrue(b);
    }

    @Test
    void insertFlight() {
        for (int i = 0; i < 200; i++) {
            new Thread(() -> {
                try {
                    BotClient bot = new BotClient("admin");
                    bot.insertFlight(
                            RandomUtils.randomString(),
                            RandomUtils.randomString(),
                            RandomUtils.randomInt(1, 20)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }


        System.out.println("\t\t****** Lista de voos disponíveis ******");
        for (Tuple<String, String> tup : this.botClient.getFlights()) {
            System.out.println("Origem -> " + tup.getX() + "  Destino -> " + tup.getY());
        }
    }

    @AfterAll
    void after() {
        this.server.stop();
    }
}
