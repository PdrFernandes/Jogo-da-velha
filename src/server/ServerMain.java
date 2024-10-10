package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.HashMap;

public class ServerMain {

    static int count = 0;
    static int port = 12345;
    public static HashMap<String, Socket> onlineClients = new HashMap<>();
    public static HashMap<String, ThreadServer> threadsOline = new HashMap<>();

    public static void main(String[] args) throws IOException, SQLException {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true){
            Socket socket;
            socket = serverSocket.accept();

            ThreadServer threadServer = new ThreadServer(socket);
            System.out.println(threadServer.getName() + " conectado");
            threadServer.start();
        }
    }
}
