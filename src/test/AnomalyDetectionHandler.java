package test;

import java.io.*;
import java.util.Scanner;
import test.Commands.DefaultIO;
import test.Server.ClientHandler;

public class AnomalyDetectionHandler implements ClientHandler {

    public class SocketIO implements DefaultIO {
        Scanner in;
        PrintWriter out;

        public SocketIO(InputStream inFromClient, OutputStream outToClient) {
            out = new PrintWriter(new OutputStreamWriter(outToClient));
            in = new Scanner(new InputStreamReader(inFromClient));
        }

        @Override
        public String readText() {
            return in.nextLine();
        }

        @Override
        public void write(String text) {
            out.print(text);
            out.flush();
        }

        @Override
        public float readVal() {
            return in.nextFloat();
        }

        @Override
        public void write(float val) {
            out.print(val);
            out.flush();
        }

        public void close() {
            in.close();
            out.close();
        }
    }

    SocketIO socketIO;
    @Override
    public void communication(InputStream inFromClient, OutputStream outToClient, String exitStr) {
        socketIO = new SocketIO(inFromClient,outToClient);
        CLI menu = new CLI(socketIO);
        menu.start();
        menu.dio.write(exitStr);
        menu.dio.write("\n");
//        socketIO.write(exitStr);
//        socketIO.write("\n");
        //socketIO.close();
    }
}