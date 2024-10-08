package server;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ServerMain {

    static int count = 0;
    static int port = 12345;
    public static HashMap<String, Socket> onlineClients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true){
            Socket socket;
            socket = serverSocket.accept();

            ThreadServer threadServer = new ThreadServer(socket);
            count++;

            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entrada));
            String name = bufferedReader.readLine();

            threadServer.setName(name);
            onlineClients.put(threadServer.getName(), socket);
            System.out.println(threadServer.getName() + " conectado");
            threadServer.start();
        }
    }
}
