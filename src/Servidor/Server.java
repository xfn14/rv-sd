package Servidor;


import Cliente.AccountManager;
import Cliente.Connection;
import Utils.UserNaoExisteException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String args[]) throws IOException {
        ServerSocket socket = new ServerSocket(12345);
        AccountManager manager = new AccountManager();

        while (true) {
            Socket s = socket.accept();
            Connection c = new Connection(s);

            Runnable worker = () -> {
                try (c) {
                    for(;;){
                        Connection.Frame frame = c.receive();
                        int tag = frame.tag;
                        switch(tag){
                            case 0 ->{
                                DataInputStream buffer = new DataInputStream(new ByteArrayInputStream(frame.data));
                                String username = buffer.readUTF();
                                String password = buffer.readUTF();

                                manager.createAccount(username, password);
                                System.out.println("Conta criada com o user : " + username + " e password " + password);

                            }
                            case 1 ->{
                                DataInputStream buffer = new DataInputStream(new ByteArrayInputStream(frame.data));
                                String username = buffer.readUTF();
                                String password = buffer.readUTF();

                                ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
                                DataOutputStream out = new DataOutputStream(bufOut);

                                try{
                                    manager.login(username,password);
                                    out.writeBoolean(true);
                                    out.flush();
                                    c.send(tag,bufOut.toByteArray());
                                    System.out.println("Login successful");

                                }catch (UserNaoExisteException u){
                                    out.writeBoolean(false);
                                    out.flush();
                                    c.send(tag, bufOut.toByteArray());
                                }
                            }
                        }
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
