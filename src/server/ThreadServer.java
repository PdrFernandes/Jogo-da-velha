package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static server.ServerMain.onlineClients;

public class ThreadServer extends Thread{
    //private static HashMap<String, Socket> othersOnlineClients = new HashMap<>();
    Socket socket;
    boolean flag = true;
    String msg = "";

    public ThreadServer (Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        System.out.println(onlineClients);

        try {
            while (flag) {
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                msg = bufferedReader.readLine();
                String[] msgFields = msg.split(";");

                switch (msgFields[0]) {
                    case "A":
                        sendMsgToClient(msg, msgFields[2]);
                        break;
                    case "B":
                        sendDisplayOnlineClients();
                        break;
                    case "C":
                        playGame(msgFields);
                        break;
                    case "D":
                        disconnect();
                        flag = false;
                        continue;
                    case "Z":
                        break;
                    default:
                        System.out.println("Mensagem de " + Thread.currentThread().getName() + ": " + Arrays.toString(msgFields));
                        break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendMsgToClient (String msg, String dest) {
        try {
            if (onlineClients.get(dest) != null) {
                DataOutputStream outputStream = new DataOutputStream(onlineClients.get(dest).getOutputStream());
                outputStream.writeBytes(msg + "\n");
                System.out.println(msg);
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendDisplayOnlineClients () {

        String resultado = String.join(";", onlineClients.keySet());

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("B;" + resultado + "\n");
            System.out.println(msg);
        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void disconnect() {
        try {
            onlineClients.remove(this.getName());

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("D\n");

            this.socket.close();

            System.out.println("Disconnecting " + this.getName());
            System.out.println(onlineClients);
        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void playGame(String[] msgFields){
        if (onlineClients.get(msgFields[2]) != null){
            GameHandler gameHandler = new GameHandler(this.socket, onlineClients.get(msgFields[2]));
            gameHandler.innit();
        }
    }

}
