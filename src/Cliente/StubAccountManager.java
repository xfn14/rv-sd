package Cliente;

import Utils.Demultiplexer;
import Utils.IAccountsManager;

import java.io.*;

public class StubAccountManager implements IAccountsManager {
    private final Demultiplexer demultiplexer;
    private final ByteArrayOutputStream buffer;
    private final DataOutputStream out;
    private boolean isAdmin;

    public StubAccountManager(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        this.buffer = new ByteArrayOutputStream();
        this.out = new DataOutputStream(this.buffer);
        this.isAdmin = false;
    }

    public boolean createAccount(String username, String password) {
        try {
            this.out.writeUTF(username);
            this.out.writeUTF(password);
            this.out.flush();

            this.demultiplexer.send(0, this.buffer);
            byte[] data = this.demultiplexer.receive();

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

            return in.readBoolean();
        } catch (IOException e) {
            System.out.println("Erro fatal");
        }

        return false;
    }

    public int login(String username, String password) {
        try {
            this.out.writeUTF(username);
            this.out.writeUTF(password);
            this.out.flush();
            System.out.println("Stub" + username + "-" + password);

            this.demultiplexer.send(1, this.buffer);

            byte[] data = this.demultiplexer.receive();

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

            int status = in.readInt();
            if (status == ADMINISTRATOR_ACCOUNT)
                this.isAdmin = true;
            return status;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IAccountsManager.INVALID_CREDENTIALS;
    }

    public boolean isAdmin(String username) {
        return this.isAdmin;
    }
}
