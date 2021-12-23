import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class Client {
    public static void main(String[] args) throws IOException {
        Socket soc = new Socket("localhost",11111);
        Connection connection = new Connection(soc);

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("A -> Criar conta");
        String option = stdin.readLine();

        switch (option){
            case "A" -> {
                System.out.println("Introduza o username : ");
                String username = stdin.readLine();
                System.out.println("Introduza a password : ");
                String password = stdin.readLine();

            }
        }

    }
}
