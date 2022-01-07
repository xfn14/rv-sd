package Cliente;

import Utils.Connection;
import Utils.IFlightsManager;
import Utils.Tuple;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StubFlightsManager implements IFlightsManager {

    private final Connection connection;
    private final ByteArrayOutputStream buffer;
    private final DataOutputStream out;

    public StubFlightsManager(Connection c) {
        this.connection = c;
        this.buffer = new ByteArrayOutputStream();
        this.out = new DataOutputStream(buffer);
    }

    public boolean insertFlight(String username, String origin, String destination, int maxCapacity) {
        boolean status = false;
        try {
            out.writeUTF(username);
            out.writeUTF(origin);
            out.writeUTF(destination);
            out.writeInt(maxCapacity);

            out.flush();
            connection.send(4, buffer);

            Connection.Frame frame = connection.receive();

            assert (frame.tag == 4);

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(frame.data));
            status = in.readBoolean();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    public String bookFlight(String username, List<String> journeys, int begin, int end) {
        String code = null;
        try {
            out.writeUTF(username);
            out.writeInt(journeys.size());
            for (String string : journeys)
                out.writeUTF(string);
            out.writeInt(begin);
            out.writeInt(end);

            out.flush();
            connection.send(2, buffer);

            Connection.Frame frame = connection.receive();

            assert (frame.tag == 2);

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(frame.data));
            code = in.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return code;
    }

    public boolean cancelBooking(String username, String code) {
        boolean status = false;
        try {
            out.writeUTF(username);
            out.writeUTF(code);
            System.out.println("Stub " + code);

            out.flush();
            connection.send(3, buffer);

            Connection.Frame frame = connection.receive();

            assert (frame.tag == 3);
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(frame.data));
            status = in.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    public void cancelDay(String username, int day){
        try{
            out.writeUTF(username);
            out.writeInt(day);

            out.flush();
            connection.send(5, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Tuple<String,String>> getFlights(){
        List<Tuple<String, String>> list = null;
        try{
            connection.send(6, buffer);
            Connection.Frame frame = connection.receive();

            assert (frame.tag == 6);
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(frame.data));

            int size = in.readInt();
            list = new ArrayList<>(size);

            for(int i = 0; i < size; ++i)
                list.add(new Tuple<>(in.readUTF(), in.readUTF()));
        }catch (IOException e) {
            e.printStackTrace();
        }

        return list;


    }
}
