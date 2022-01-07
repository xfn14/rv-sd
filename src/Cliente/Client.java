package Cliente;

import Servidor.FlightsManager;
import UI.Menu;
import Utils.Connection;
import Utils.IAccountsManager;
import Utils.IFlightsManager;
import Utils.Tuple;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Client {

    private IAccountsManager accountsManager;
    private IFlightsManager flightsManager;
    private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    private String username;

    public static void main(String[] args) throws IOException {

        Socket soc = new Socket("localhost", 12345);
        Connection connection = new Connection(soc);

        Client client = new Client(connection);

        Menu mainMenu = new Menu(new String[]{
                "Registrar conta",
                "Login"
        });
        mainMenu.setHandler(1, client::registerAccount);
        mainMenu.setHandler(2, client::login);
        mainMenu.run();

    }

    public Client(Connection c) {
        this.accountsManager = new StubAccountManager(c);
        this.flightsManager = new StubFlightsManager(c);
    }

    public void showMenuReservations() {
        Menu reservs = new Menu(new String[]{
                "Reservar",
                "Cancelar",
                "Listar"
        });
        reservs.setHandler(1, this::bookFlight);
        reservs.setHandler(2, this::cancelFlight);
        reservs.setHandler(3, this::getFlights);
        reservs.run();
    }

    public void showMenuAdmin(){
        Menu admin = new Menu(new String[]{
                "Inserir voo",
                "Cancelar dia"
        });
        admin.setHandler(1, this::insertFlight);
        admin.setHandler(2, this::cancelDay);
        admin.run();

    }

    public void cancelDay(){
        try{
            System.out.println("Introduza o dia que pretende fechar : ");
            int day = Integer.parseInt(stdin.readLine());

            this.flightsManager.cancelDay(this.username, day);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void insertFlight(){
        try{
            System.out.println("Introduza a origem : ");
            String origin = stdin.readLine();
            System.out.println("Introduza o destino : ");
            String destination = stdin.readLine();
            System.out.println("Introduza a capacidade máxima : ");
            int max = Integer.parseInt(stdin.readLine());

            boolean status = this.flightsManager.insertFlight(this.username, origin, destination, max);

            if(!status){
                System.out.println("Erro ! Voo já existente!");
            }else{
                System.out.println("Voo inserido com sucesso!");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void bookFlight() {
        try {
            System.out.println("Introduza os voos (separados por ->) : ");
            String journeyU = stdin.readLine();
            List<String> journey = Arrays.stream(journeyU.split("->")).toList();
            System.out.println(journey);
            System.out.println("Introduza o dia inicial : ");
            int begin = Integer.parseInt(stdin.readLine());
            System.out.println("Introduza o dia final : ");
            int end = Integer.parseInt(stdin.readLine());

            String code = flightsManager.bookFlight(this.username, journey, begin, end);

            if (code.equals("")) {
                System.out.println("Erro a reservar voo!");
            } else {
                System.out.println("Código de reserva : " + code);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelFlight(){
        try{
            System.out.println("Introduza o código do voo : ");
            String code = stdin.readLine();

            boolean status = flightsManager.cancelBooking(this.username, code);

            if (!status){
                System.out.println("Erro no cancelamento!");
            }else{
                System.out.println("Cancelamento feito com sucesso !!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getFlights(){
        List<Tuple<String, String>> flights = flightsManager.getFlights();

        System.out.println("\t\t****** Lista de voos disponíveis ******");
        for(Tuple<String, String> tup : flights){
            System.out.println("Origem -> " + tup.getX() + "  Destino -> " + tup.getY());
        }
    }

    public void registerAccount () {
        try {
            System.out.println("Introduza o username : ");
            String username = stdin.readLine();
            System.out.println("Introduza a password : ");
            String password = stdin.readLine();

            accountsManager.createAccount(username, password);
            this.username = username;

            this.showMenuReservations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void login () {
        try {
            System.out.println("Introduza o username : ");
            String username = stdin.readLine();
            System.out.println("Introduza a password : ");
            String password = stdin.readLine();

            int status = accountsManager.login(username, password);

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
