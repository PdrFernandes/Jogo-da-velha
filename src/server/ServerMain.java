package server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {

    static int port = 12345;
    public static HashMap<String, Socket> onlineClients = new HashMap<>();
    public static HashMap<String, ThreadServer> threadsOline = new HashMap<>();

    public static void main(String[] args) throws IOException {
        //pool para melhor controle das threads
        ExecutorService pool = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("IP do servidor: " + serverSocket.getInetAddress().getHostName());
        System.out.println("Porta: " + serverSocket.getLocalPort());

        while (true){
            Socket socket;
            socket = serverSocket.accept();

            pool.submit(new ThreadServer(socket));

            System.out.println(pool);
        }
    }
}
