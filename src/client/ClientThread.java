package client;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.*;

import static client.ClientMain.flag;
import static server.ServerMain.onlineClients;

// Cuida das menssagens a serem recebidas do servidor
public class ClientThread extends Thread{
    Socket socket;
    String msg = "";

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            while (flag) {
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                msg = bufferedReader.readLine();
                String[] msgFields;

                if (msg != null) {
                    msgFields = msg.split(";");
                } else {
                    msgFields = new String[1];
                    msgFields[0] = "D";
                }

                switch (msgFields[0]) {
                    case "A":
                        System.out.println("Mensagem de " + msgFields[1] + ": " + msgFields[3]);
                        break;
                    case "B":
                        displayOnlineClients(msgFields);
                        break;
                    case "C":
                        break;
                    case "D":
                        System.out.println("Você foi desconectado");
                        continue;
                    default:
                        System.out.println(msg);
                        break;
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void displayOnlineClients (String[] msgField){
        System.out.println("Clientes online: ");
        for (int i = 1; i < msgField.length; i++) {
            if (Objects.equals(msgField[i], this.getName())) System.out.println(msgField[i] + " (você)");
            else System.out.println(msgField[i]);
        }
    }
}
