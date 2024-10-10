package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static server.Server.*;
import static server.ServerMain.onlineClients;
import static server.ServerMain.threadsOline;

public class ThreadServer extends Thread{
    Socket socket;
    boolean flag = true;
    String msg = "";
    String[] tabuleiro;
    boolean isplaying = false;

    public ThreadServer (Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        System.out.println(onlineClients);
        String name = "";

        try {
            while (flag) {
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                name = bufferedReader.readLine();

                entrada = new DataInputStream(socket.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                String psswrd = bufferedReader.readLine();

                System.out.println(name + " " + psswrd);

                List<Map<String, Object>> loginList = sel_login(name, psswrd);

                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println(loginList);

                if (!loginList.isEmpty()  && !onlineClients.containsKey(name)) {
                    outputStream.writeBytes("E;1\n");
                    System.out.println("E;1\n");
                    flag = false;
                } else {
                    outputStream.writeBytes("E;0\n");
                    System.out.println("E;0\n");
                }
            }

            flag = true;
            this.setName(name);
            onlineClients.put(name, socket);
            threadsOline.put(name, this);

            Thread.sleep(100);

            sendDisplayOnlineClients();

            while (flag) {
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                msg = bufferedReader.readLine();
                String[] msgFields = msg.split(";");

                switch (msgFields[0]) {
                    case "A":
                        sendMsgToClient(msgFields);
                        break;
                    case "B":
                        sendDisplayOnlineClients();
                        break;
                    case "C":
                        if (Objects.equals(msgFields[1], "1")) playGame(msgFields);
                        else sendMoveGame(msgFields);
                        break;
                    case "D":
                        disconnect();
                        flag = false;
                        continue;
                    default:
                        System.out.println("Mensagem de " + Thread.currentThread().getName() + ": " + Arrays.toString(msgFields));
                        break;
                }
            }
        } catch (IOException | InterruptedException | SQLException ex ) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendMsgToClient (String[] msgFields) {
        try {
            if (onlineClients.get(msgFields[2]) != null) {
                DataOutputStream outputStream = new DataOutputStream(onlineClients.get(msgFields[2]).getOutputStream());
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
            threadsOline.remove(this.getName());

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("D\n");

            System.out.println("Disconnecting " + this.getName());
            System.out.println(onlineClients);


        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void playGame(String[] msgFields){
        if (onlineClients.get(msgFields[3]) != null && !isplaying){
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;0;1;" + msgFields[3] + ";" + "X" + "\n");

                Thread.sleep(100);

                outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;0;0;" + msgFields[2] + ";" + "O" + "\n");

                for (ThreadServer t : threadsOline.values()) {
                    if (t.getName().equals(msgFields[3])) {
                        t.isplaying = true;
                        t.tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                    }
                }

                isplaying = true;
                tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};

            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                if (isplaying) {
                    outputStream.writeBytes("C;1;1;" + msgFields[3] + "\n");
                } else {
                    outputStream.writeBytes("C;1;0;" + msgFields[3] + "\n");
                }
            } catch (IOException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sendMoveGame(String[] msgFields) {
        tabuleiro[Integer.parseInt(msgFields[4])] = msgFields[5];

        System.out.println(Arrays.toString(tabuleiro));

        if (checkWinner(msgFields[5])){
            try {
                System.out.println("Winner");
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;1;1;" + msgFields[3] + "\n");

                Thread.sleep(100);

                outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;1;0;" + msgFields[2] + "\n");

                for (ThreadServer t : threadsOline.values()) {
                    if (t.getName().equals(msgFields[3])) {
                        t.isplaying = false;
                        t.tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                    }
                }

                tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                isplaying = false;
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (isBoardFull()){
            try {
                System.out.println("Draw");
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;2\n");

                Thread.sleep(100);

                outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;2\n");

                for (ThreadServer t : threadsOline.values()) {
                    if (t.getName().equals(msgFields[3])) {
                        t.isplaying = false;
                        t.tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                    }
                }

                tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                isplaying = false;
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                System.out.println("Adversário");
                DataOutputStream outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;0;1;" + msgFields[4] + ";" + msgFields[5] + "\n");
            } catch (IOException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean isBoardFull() {
        String[] resultado = Arrays.stream(tabuleiro).filter(s -> !s.equals("-")).toArray(String[]::new);
        System.out.println("check:" + Arrays.toString(resultado));

        return resultado.length > 4;
    }

    private boolean checkWinner(String currentPlayer) {
        int[][] winningPositions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Linhas
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Colunas
                {0, 4, 8}, {2, 4, 6}             // Diagonais
        };

        for (int[] wp : winningPositions) {
            if (Objects.equals(tabuleiro[wp[0]], currentPlayer) && Objects.equals(tabuleiro[wp[1]], currentPlayer) && Objects.equals(tabuleiro[wp[2]], currentPlayer)) {
                return true;
            }
        }
        return false;
    }

}
