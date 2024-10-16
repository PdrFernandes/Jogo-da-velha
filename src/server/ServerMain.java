package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

public class ServerMain {

    static int port = 12345;
    public static HashMap<String, Socket> onlineClients = new HashMap<>();
    public static HashMap<String, ThreadServer> threadsOline = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("IP do servidor: " + serverSocket.getInetAddress().getHostName());
        System.out.println("Porta: " + serverSocket.getLocalPort());

        while (true){
            Socket socket;
            socket = serverSocket.accept();

            ThreadServer threadServer = new ThreadServer(socket);
            System.out.println(threadServer.getName() + " conectado");
            threadServer.start();
        }
    }
}
