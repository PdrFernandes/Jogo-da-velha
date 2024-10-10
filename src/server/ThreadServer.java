package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static server.Server.*;
import static server.ServerMain.onlineClients;

public class ThreadServer extends Thread{
    //private static HashMap<String, Socket> othersOnlineClients = new HashMap<>();
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
                    case "Z":
                        break;
                    default:
                        System.out.println("Mensagem de " + Thread.currentThread().getName() + ": " + Arrays.toString(msgFields));
                        break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("D\n");

            System.out.println("Disconnecting " + this.getName());
            System.out.println(onlineClients);


        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void playGame(String[] msgFields){
        if (onlineClients.get(msgFields[3]) != null){
            try {
                isplaying = true;
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;0;1;" + msgFields[3] + ";" + "X" + "\n");

                Thread.sleep(500);

                outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;0;0;" + msgFields[2] + ";" + "O" + "\n");

                tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;1;" + msgFields[3] + "\n");
            } catch (IOException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sendMoveGame(String[] msgFields) {
        if (!isplaying) {
            tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
            isplaying = true;
        }
        tabuleiro[Integer.parseInt(msgFields[4])] = msgFields[5];

        if (checkWinner(msgFields[5])){
            //outputStream.writeBytes( "C;0;" + jusernameTextField.getText() + ";" + oponente + ";" + botao + ";" + player + "\n");
            try {
                System.out.println("Winner");
                ///////JOGADOR Q ACABOU DE JOGAR GANHOU
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;1;" + msgFields[3] + "\n");
            } catch (IOException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (isBoardFull()){
            try {
                System.out.println("Draw");
                /////EMPATE
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;1;" + msgFields[3] + "\n");

                outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;1;" + msgFields[3] + "\n");
            } catch (IOException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                System.out.println("Adversário");
                /////ENVIA JOGADA PRO ADVERSÀRIO
                DataOutputStream outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                                                     //nome p qm vai      jogada (botão)         X ou O
                outputStream.writeBytes("C;0;1;" + msgFields[3] + ";" + msgFields[4] + ";" + msgFields[5] + "\n");
            } catch (IOException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean isBoardFull() {
        for (String c : tabuleiro) {
            if (Objects.equals(c, "-")) {
                return false;
            }
        }
        return true;
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
