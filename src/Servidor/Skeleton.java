package Servidor;

import Utils.Connection;
import Utils.IAccountsManager;
import Utils.IFlightsManager;
import Utils.Tuple;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Skeleton {
    private final IAccountsManager accountsManager;
    private final IFlightsManager flightsManager;

    public Skeleton() {
        this.accountsManager = new AccountsManager();
        this.flightsManager = new FlightsManager();
    }

    public void handle(Connection c) throws IOException {
        Connection.Frame frame = c.receive();
        int tag = frame.tag;
        DataInputStream buffer = new DataInputStream(new ByteArrayInputStream(frame.data));

        ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bufOut);

        switch (tag) {
            case 0 -> {
                String username = buffer.readUTF();
                String password = buffer.readUTF();

                this.accountsManager.createAccount(username, password);
                System.out.println("Conta criada com o user : " + username + " e password " + password);
            }

            case 1 -> {
                String username = buffer.readUTF();
                String password = buffer.readUTF();

                System.out.println("Sk" + username + "-" + password);

                out.writeInt(this.accountsManager.login(username, password));
            }

            case 2 -> {
                String username = buffer.readUTF();
                int size = buffer.readInt();
                List<String> journey = new ArrayList<>(size);

                for (int i = 0; i < size; ++i)
                    journey.add(buffer.readUTF());

                int begin = buffer.readInt();
                int end = buffer.readInt();

                String code = this.flightsManager.bookFlight(username, journey, begin, end);

                out.writeUTF(code);
            }

            case 3 -> {
                String username = buffer.readUTF();
                String code = buffer.readUTF();

                boolean status = this.flightsManager.cancelBooking(username, code);

                out.writeBoolean(status);
            }

            case 4 -> {
                String username = buffer.readUTF();
                String origin = buffer.readUTF();
                String destination = buffer.readUTF();
                int capacity = buffer.readInt();

                boolean status = false;

                if (this.accountsManager.isAdmin(username)) {
                    status = this.flightsManager.insertFlight(username, origin, destination, capacity);
                }

                out.writeBoolean(status);
            }

            case 5 -> {
                String username = buffer.readUTF();
                int day = buffer.readInt();

                boolean status = false;

                if (this.accountsManager.isAdmin(username)) {
                    this.flightsManager.cancelDay(username, day);
                    status = true;
                }

                out.writeBoolean(status);
            }

            case 6 -> {
                List<Tuple<String, String>> list = this.flightsManager.getFlights();

                out.writeInt(list.size());
                for(Tuple<String, String> tup : list){
                    out.writeUTF(tup.getX());
                    out.writeUTF(tup.getY());
                }
            }
        }
        if (out.size() > 0) {
            out.flush();
            c.send(tag, bufOut.toByteArray());
        }
    }
}
