package Cliente;

import Utils.Connection;
import Utils.IAccountsManager;

import java.io.*;

public class StubAccountManager implements IAccountsManager {

    private final Connection connection;
    private final ByteArrayOutputStream buffer;
    private final DataOutputStream out;


    public StubAccountManager(Connection c) {
        this.connection = c;
        this.buffer = new ByteArrayOutputStream();
        this.out = new DataOutputStream(buffer);
    }

    public void createAccount(String username, String password) {

        try {
            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();

            connection.send(0, buffer.toByteArray());
        } catch (IOException e) {
            System.out.println("Erro fatal");
        }

    }
    public int login(String username, String password) {

        try {
            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();

            connection.send(1, buffer.toByteArray());

            Connection.Frame frame = connection.receive();

            assert(frame.tag == 1);

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(frame.data));

            return in.readInt();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return IAccountsManager.INVALID_CREDENTIALS;
    }

}
