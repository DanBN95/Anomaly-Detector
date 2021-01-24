package test;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {

    volatile boolean stop;
    public Server() {
        stop=false;
    }
    volatile int clientLimit = 0;

    public interface ClientHandler {

        void communication(InputStream inFromClient, OutputStream outToClient, String exitStr);
    }

    private void startServer(int port, ClientHandler ch) {
        try {
            //server started
            ServerSocket server = new ServerSocket(port);

            while (!stop) {
                    server.setSoTimeout(1000);
                    if (clientLimit == 1)
                        continue;
;
                    //client connected
                    // communication with client
                    try {
                        Socket aClient = server.accept();
                        //System.out.println("Server connected!");
                        clientLimit++;

                        ch.communication(aClient.getInputStream(), aClient.getOutputStream(), "bye");

                        aClient.close();
                        //System.out.println("Server closed successfully");
                        clientLimit--;
                        stop();
                        //System.out.println("System stopped");
                    } catch (IOException e) {
                        e.printStackTrace();
                        //System.out.println("IQExeption line 50");
                        }
                }
            server.close();
        }catch (SocketTimeoutException e){
            e.printStackTrace();
            //System.out.println("IQExeption line 55");
            }
        catch (IOException e){
            e.printStackTrace();
            //System.out.println("IQExeption line 58");
            }
    }


    // runs the server in its own thread
    public void start(int port, ClientHandler ch) {
        //System.out.println("open new thread");
        new Thread(()->startServer(port,ch)).start();
    }

    public void stop() {
        stop=true;
    }
}

