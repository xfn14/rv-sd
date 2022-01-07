package Cliente;

import Utils.Connection;
import Utils.IAccountsManager;

import java.io.*;

public class StubAccountManager implements IAccountsManager {

    private final Connection connection;
    private final ByteArrayOutputStream buffer;
    private final DataOutputStream out;
    private boolean isAdmin;


    public StubAccountManager(Connection c) {
        this.connection = c;
        this.buffer = new ByteArrayOutputStream();
        this.out = new DataOutputStream(buffer);
        this.isAdmin = false;
    }

    public void createAccount(String username, String password) {

        try {
            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();

            connection.send(0, buffer);

        } catch (IOException e) {
            System.out.println("Erro fatal");
        }

    }
    public int login(String username, String password) {

        try {
            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();
            System.out.println("Stub" + username + "-" + password);

            connection.send(1, buffer);

            Connection.Frame frame = connection.receive();

            assert(frame.tag == 1);

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(frame.data));

            int status = in.readInt();
            if(status == ADMINISTRATOR_ACCOUNT){
                this.isAdmin = true;
            }
            return status;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return IAccountsManager.INVALID_CREDENTIALS;
    }

    public boolean isAdmin(String username){
        return isAdmin;
    }

}
