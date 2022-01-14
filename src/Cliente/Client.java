package Cliente;

import UI.Menu;
import Utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
    private final IAccountsManager accountsManager;
    private final IFlightsManager flightsManager;
    private final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    private String username;
    List<Tuple<Thread, ThreadedBookFlight>> booksFlights = new ArrayList<>();

    public Client(Demultiplexer demultiplexer) {
        this.accountsManager = new StubAccountManager(demultiplexer);
        this.flightsManager = new StubFlightsManager(demultiplexer);
    }

    public static void main(String[] args) throws IOException {
        Socket soc = new Socket("localhost", 12345);
        Connection conn = new Connection(soc);
        Demultiplexer demultiplexer = new Demultiplexer(conn);
        demultiplexer.start();

        Client client = new Client(demultiplexer);

        Menu mainMenu = new Menu(new String[]{
                "Registrar conta",
                "Login"
        });
        mainMenu.setHandler(1, client::registerAccount);
        mainMenu.setHandler(2, client::login);
        mainMenu.run();
    }

    public void showMenuReservations() {
        Menu reservs = new Menu(new String[]{
                "Reservar",
                "Cancelar",
                "Listar",
                "Escalas",
                "Temp Logs Reservas"
        });
        reservs.setHandler(1, this::bookFlight);
        reservs.setHandler(2, this::cancelFlight);
        reservs.setHandler(3, this::getFlights);
        reservs.setHandler(4, this::getScales);
        reservs.setHandler(5, this::getTempLogsBooksFlights);
        reservs.setPreCondition(5, () -> booksFlights.size() > 0);
        reservs.run();
    }

    public void showMenuAdmin() {
        Menu admin = new Menu(new String[]{
                "Inserir voo",
                "Cancelar dia"
        });
        admin.setHandler(1, this::insertFlight);
        admin.setHandler(2, this::cancelDay);
        admin.run();
    }

    public void getScales(){
        try{
            System.out.println("Introduza a origem : ");
            String origin = this.stdin.readLine();
            System.out.println("Introduza o destino : ");
            String destination = this.stdin.readLine();

            List<Tuple<String, String>> list = this.flightsManager.getScales(origin, destination);

            System.out.println("\t\t****** Lista de escalas disponíveis ******");

            for (Tuple<String, String> tup : list)
                System.out.println("Primeira Escala -> " + tup.getX() + "  Segunda Escala -> " + tup.getY());

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void cancelDay() {
        try {
            System.out.println("Introduza o dia que pretende fechar : ");
            int day = Integer.parseInt(this.stdin.readLine());
            this.flightsManager.cancelDay(this.username, day);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertFlight() {
        try {
            System.out.println("Introduza a origem : ");
            String origin = this.stdin.readLine();
            System.out.println("Introduza o destino : ");
            String destination = this.stdin.readLine();
            System.out.println("Introduza a capacidade máxima : ");
            int max = Integer.parseInt(this.stdin.readLine());

            boolean status = this.flightsManager.insertFlight(this.username, origin, destination, max);
            if (status) System.out.println("Voo inserido com sucesso!");
            else System.out.println("Erro ! Voo já existente!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ThreadedBookFlight implements Runnable {
        final IFlightsManager flightsManager;
        final String username;
        final List<String> journey;
        final int begin;
        final int end;
        private volatile String code;

        public ThreadedBookFlight(IFlightsManager flightsManager, String username, List<String> journey, int begin, int end) {
            this.flightsManager = flightsManager;
            this.username = username;
            this.journey = journey;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            code = flightsManager.bookFlight(username, journey, begin, end);
        }
    }

    public void getTempLogsBooksFlights() {

        for (Tuple<Thread, ThreadedBookFlight> tup: this.booksFlights) {
            Thread thread = tup.getX();
            ThreadedBookFlight threadedBookFlight = tup.getY();

            System.out.println("-------------------------------" +
                    "\nUser: " + threadedBookFlight.username +
                    "\nJourney: " + threadedBookFlight.journey +
                    "\nBegin: " + threadedBookFlight.begin +
                    "\nEnd: " + threadedBookFlight.end
                    );

            if (thread == null)
                System.out.println("Code: " + threadedBookFlight.code);
            else if (thread.isAlive())
                System.out.println("Code: --Processing--");
            else {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Code: " + threadedBookFlight.code);
            }

            System.out.println("-------------------------------");


        }
    }

    public void bookFlight() {
        try {
            System.out.println("Introduza os voos (separados por ->) : ");
            String journeyU = this.stdin.readLine();
            List<String> journey = Arrays.stream(journeyU.split("->")).map(String::strip).toList();
            System.out.println(journey);
            System.out.println("Introduza o dia inicial : ");
            int begin = Integer.parseInt(this.stdin.readLine());
            System.out.println("Introduza o dia final : ");
            int end = Integer.parseInt(this.stdin.readLine());

            ThreadedBookFlight threadedBookFlight = new ThreadedBookFlight(this.flightsManager, this.username, journey, begin, end);
            Thread thread = new Thread(threadedBookFlight);
            thread.start();

            this.booksFlights.add(new Tuple<Thread, ThreadedBookFlight>(thread, threadedBookFlight));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void cancelFlight() {
        try {
            System.out.println("Introduza o código do voo : ");
            String code = this.stdin.readLine();

            boolean status = this.flightsManager.cancelBooking(this.username, code);
            if (status) System.out.println("Cancelamento feito com sucesso !!");
            else System.out.println("Erro no cancelamento!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getFlights() {
        List<Tuple<String, String>> flights = this.flightsManager.getFlights();
        System.out.println("\t\t****** Lista de voos disponíveis ******");
        for (Tuple<String, String> tup : flights)
            System.out.println("Origem -> " + tup.getX() + "  Destino -> " + tup.getY());
    }

    public void registerAccount() {
        try {
            System.out.println("Introduza o username : ");
            String username = this.stdin.readLine();
            System.out.println("Introduza a password : ");
            String password = this.stdin.readLine();

            this.accountsManager.createAccount(username, password);
            this.username = username;

            this.showMenuReservations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void login() {
        try {
            System.out.println("Introduza o username : ");
            String username = this.stdin.readLine();
            System.out.println("Introduza a password : ");
            String password = this.stdin.readLine();

            int status = this.accountsManager.login(username, password);
            switch (status) {
                case IAccountsManager.NOT_REGISTED -> System.out.println("User não registado");
                case IAccountsManager.INVALID_CREDENTIALS -> System.out.println("Credenciais erradas");
                case IAccountsManager.NORMAL_ACCOUNT -> {
                    System.out.println("Login como normal sucedido");
                    this.username = username;
                    this.showMenuReservations();
                }
                case IAccountsManager.ADMINISTRATOR_ACCOUNT -> {
                    System.out.println("Login como administrador sucedido");
                    this.username = username;
                    this.showMenuAdmin();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
