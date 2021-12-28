package Cliente;

import UI.Menu;
import Utils.Connection;
import Utils.IAccountsManager;

import java.io.*;
import java.net.Socket;

public class Client {

    private IAccountsManager accountsManager;
    private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

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
    }

    public void showMenuReservations(){
        Menu reservs = new Menu(new String[]{
                "Reservar",
                "Cancelar",
                "Listar"
        });
        reservs.run();
    }

    public void registerAccount() {
        try {
            System.out.println("Introduza o username : ");
            String username = stdin.readLine();
            System.out.println("Introduza a password : ");
            String password = stdin.readLine();

            accountsManager.createAccount(username, password);
            this.showMenuReservations();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void login() {
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
                    this.showMenuReservations();
                }
                case IAccountsManager.ADMINISTRATOR_ACCOUNT -> System.out.println("Login como administrador sucedido");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
