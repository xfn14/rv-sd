package Cliente;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements AutoCloseable{

    private Socket socket;

    private ReentrantLock lockRead = new ReentrantLock();
    private ReentrantLock lockWrite = new ReentrantLock();

    private DataOutputStream out;
    private DataInputStream in;


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
    public void send(int tag, byte[] data) throws IOException{
        try{
            this.lockWrite.lock();
            this.out.writeInt(4 + data.length);
            this.out.writeInt(tag);
            this.out.write(data);
            this.out.flush();
        }finally {
            this.lockWrite.unlock();
        }
    }
    public Frame receive() throws IOException{
        try{
            this.lockRead.lock();
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