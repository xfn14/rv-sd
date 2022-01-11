package Servidor;


import Utils.Connection;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private boolean running = true;

    public static void main(String args[]) throws IOException {
        Server server = new Server();
        server.initServer();
    }

    public void initServer() throws IOException {
        ServerSocket socket = new ServerSocket(12345);
        Skeleton sk = new Skeleton();

        while (this.running) {
            Socket s = socket.accept();
            Connection c = new Connection(s);

            Runnable worker = () -> {
                try (c) {
                    for (; ; ) {
                        sk.handle(c);
                    }
                }catch (EOFException ignored){
                }catch (Exception e){
                    e.printStackTrace();
                }
            };

            for(int i = 0; i < 3; ++i)
                new Thread(worker).start();

        }
        socket.close();
    }

    public void stop(){
        this.running = false;
    }
}
