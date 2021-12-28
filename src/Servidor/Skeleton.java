package Servidor;

import Utils.Connection;
import Utils.IAccountsManager;

import java.io.*;

public class Skeleton {

    final private IAccountsManager accountsManager;

    public Skeleton() {
        this.accountsManager = new AccountsManager();
    }

    public void handle(Connection c) throws IOException {

        Connection.Frame frame = c.receive();
        int tag = frame.tag;
        DataInputStream buffer = new DataInputStream(new ByteArrayInputStream(frame.data));

        switch (tag) {
            case 0 -> {
                String username = buffer.readUTF();
                String password = buffer.readUTF();

                accountsManager.createAccount(username, password);
                System.out.println("Conta criada com o user : " + username + " e password " + password);

            }
            case 1 -> {
                String username = buffer.readUTF();
                String password = buffer.readUTF();

                ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bufOut);

                out.writeInt(accountsManager.login(username, password));

                out.flush();
                c.send(tag, bufOut.toByteArray());
            }
        }

    }
}
