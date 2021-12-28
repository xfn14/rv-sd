package Servidor;


import Utils.Connection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String args[]) throws IOException {
        ServerSocket socket = new ServerSocket(12345);
        Skeleton sk = new Skeleton();

        while (true) {
            Socket s = socket.accept();
            Connection c = new Connection(s);

            Runnable worker = () -> {
                try (c) {
                    for(;;){
                        sk.handle(c);
                    }
                }catch (Exception ignored){
                    ignored.printStackTrace();
                }
            };

            for(int i = 0; i < 3; ++i)
               new Thread(worker).start();

        }
    }
}
