package Utils;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements AutoCloseable{

    private final Socket socket;

    private final ReentrantLock lockRead = new ReentrantLock();
    private final ReentrantLock lockWrite = new ReentrantLock();

    private final DataOutputStream out;
    private final DataInputStream in;


    public static class Frame{
        public final int tag;
        public final byte[] data;

        public Frame(int tag, byte[] data){
            this.tag = tag;
            this.data = data;
        }
    }
    public Connection(Socket socket) throws IOException{
        this.socket = socket;
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public void send (Frame frame) throws IOException{
        send(frame.tag,frame.data);
    }

    public void send(int tag, ByteArrayOutputStream byteArray) throws IOException{
        this.send(tag, byteArray.toByteArray());
        byteArray.reset();
    }

    public void send(int tag, byte[] data) throws IOException{

        this.lockWrite.lock();
        try{
            this.out.writeInt(4 + data.length);
            this.out.writeInt(tag);
            this.out.write(data);
            this.out.flush();
        }finally {
            this.lockWrite.unlock();
        }
    }
    public Frame receive() throws IOException{

        this.lockRead.lock();
        try{
            int size = this.in.readInt();
            byte[] data = new byte[size - 4];
            int tag = this.in.readInt();
            this.in.readFully(data);

            return new Frame(tag,data);

        }finally {
            this.lockRead.unlock();
        }
    }
    public void close() throws IOException{
        this.socket.close();
    }
}