package Cliente;

import UI.Menu;

import java.io.*;
import java.net.Socket;

public class Client {

    private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {

        Socket soc = new Socket("localhost", 12345);
        Connection connection = new Connection(soc);

        Client client = new Client();


        Menu mainMenu = new Menu(new String[]{
                "Registrar conta",
                "Login"
        });
        mainMenu.setHandler(1, () -> client.registerAccount(connection));
        mainMenu.setHandler(2, () -> client.login(connection));
        mainMenu.run();

    }

    public void showMenuReservations(){
        Menu reservs = new Menu(new String[]{
                "Reservar",
                "Cancelar",
                "Listar"
        });
        reservs.run();
    }

    public void registerAccount(Connection connection) {
        try {
            System.out.println("Introduza o username : ");
            String username = stdin.readLine();
            System.out.println("Introduza a password : ");
            String password = stdin.readLine();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(buffer);

            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();

            connection.send(0, buffer.toByteArray());
            this.showMenuReservations();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void login(Connection connection) {
        try {
            System.out.println("Introduza o username : ");
            String username = stdin.readLine();
            System.out.println("Introduza a password : ");
            String password = stdin.readLine();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(buffer);

            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();

            connection.send(1, buffer.toByteArray());

            Connection.Frame frame = connection.receive();
            if(frame.tag == 1) {
                ByteArrayInputStream bufIn = new ByteArrayInputStream(frame.data);
                DataInputStream in = new DataInputStream(bufIn);
                if (in.readBoolean()) {
                    System.out.println("Login successful");
                    this.showMenuReservations();
                }else{
                    System.out.println("Login failed");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
