package Cliente;

import Utils.Demultiplexer;
import Utils.IFlightsManager;
import Utils.Tuple;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StubFlightsManager implements IFlightsManager {
    private final Demultiplexer demultiplexer;
    private final ByteArrayOutputStream buffer;
    private final DataOutputStream out;

    public StubFlightsManager(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        this.buffer = new ByteArrayOutputStream();
        this.out = new DataOutputStream(this.buffer);
    }

    public boolean insertFlight(String username, String origin, String destination, int maxCapacity) {
        boolean status = false;
        try {
            this.out.writeUTF(username);
            this.out.writeUTF(origin);
            this.out.writeUTF(destination);
            this.out.writeInt(maxCapacity);

            this.out.flush();
            this.demultiplexer.send(4, this.buffer);

            byte[] data = this.demultiplexer.receive();

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            status = in.readBoolean();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    public String bookFlight(String username, List<String> journeys, int begin, int end) {
        String code = null;
        try {
            this.out.writeUTF(username);
            this.out.writeInt(journeys.size());
            for (String string : journeys)
                this.out.writeUTF(string);
            this.out.writeInt(begin);
            this.out.writeInt(end);

            this.out.flush();
            this.demultiplexer.send(2, this.buffer);
            byte[] data = this.demultiplexer.receive();

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            code = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    public boolean cancelBooking(String username, String code) {
        boolean status = false;
        try {
            this.out.writeUTF(username);
            this.out.writeUTF(code);
            System.out.println("Stub " + code);

            this.out.flush();
            this.demultiplexer.send(3, this.buffer);

            byte[] data = this.demultiplexer.receive();

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            status = in.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    public void cancelDay(String username, int day) {
        try {
            this.out.writeUTF(username);
            this.out.writeInt(day);

            this.out.flush();
            this.demultiplexer.send(5, this.buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Tuple<String, String>> getFlights() {
        List<Tuple<String, String>> list = null;
        try {
            this.demultiplexer.send(6, this.buffer);
            byte[] data = this.demultiplexer.receive();

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

            int size = in.readInt();
            list = new ArrayList<>(size);

            for (int i = 0; i < size; ++i)
                list.add(new Tuple<>(in.readUTF(), in.readUTF()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Tuple<String, String>> getScales(String origin, String destination){
        List<Tuple<String, String>> list = new ArrayList<>();

        try{
            this.out.writeUTF(origin);
            this.out.writeUTF(destination);
            this.demultiplexer.send(7, this.buffer);
            byte[] data = this.demultiplexer.receive();

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

            int size = in.readInt();
            for(int i = 0; i < size; ++i){
                list.add(new Tuple<>(in.readUTF(), in.readUTF()));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }
}
