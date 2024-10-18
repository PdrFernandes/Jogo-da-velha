package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static server.BDServer.*;
import static server.ServerMain.onlineClients;
import static server.ServerMain.threadsOline;

public class ThreadServer extends Thread{
    Socket socket;
    boolean flag = true;
    String msg = "";
    String[] tabuleiro;
    boolean isplaying = false;
    ThreadServer oponente;

    public ThreadServer (Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        String name = "";

        try {
            //Loop reponsavel pelo login/criar login
            while (flag) {
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                String acao = bufferedReader.readLine();

                entrada = new DataInputStream(socket.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                name = bufferedReader.readLine();

                entrada = new DataInputStream(socket.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                String psswrd = bufferedReader.readLine();

                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                System.out.println(acao +" " + name + " " + psswrd);

                if (Objects.equals(acao, "L")) {
                    List<Map<String, Object>> loginList = sel_login(name, psswrd);

                    if (!loginList.isEmpty()  && !onlineClients.containsKey(name)) {
                        outputStream.writeBytes("E;1\n");
                        flag = false;
                    } else {
                        outputStream.writeBytes("E;0\n");
                    }
                } else {
                    List<Map<String, Object>> listaUsuarios = sel_usuario_username_literal(name);

                    if(listaUsuarios.isEmpty()) {
                        //Caso o usuário não esteja em uso, cria o usuário
                        ins_usuario("", name, psswrd);
                        outputStream.writeBytes("F;1\n");

                    } else {
                        outputStream.writeBytes("F;0\n");
                    }

                }

            }

            //Configura a thread
            flag = true;
            this.setName(name);
            onlineClients.put(name, socket);
            threadsOline.put(name, this);

            Thread.sleep(100);

            sendDisplayOnlineClients();

            Thread.sleep(200);

            sendDisplayFirends();

            //Loop reponsavel por receber as mensagem do cliente e decodificar
            while (flag) {
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                msg = bufferedReader.readLine();
                String[] msgFields = msg.split(";");

                System.out.println(Arrays.toString(msgFields));

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
                    case "E":
                        adicionarAmigo(msgFields);
                    default:
                        break;
                }
            }
        } catch (IOException | InterruptedException | SQLException ex ) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            disconnectException();
        }
    }

    //Chamada para adicionar amizades
    //Tenta adicionar amizade e envia para o cliente se deu erro ou nao
    private void adicionarAmigo (String[] msgFields) {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            int id_usuario = Integer.parseInt(
                    (sel_usuario_username_literal(this.getName()).getFirst()).get("id_usuario").toString());
            List<Map<String, Object>> listaAmigo =sel_usuario_username_literal(msgFields[1]);

            //Caso não encontre o usuário
            if (listaAmigo.isEmpty()) {
                outputStream.writeBytes("G;0\n");
                return;
            }

            int id_amigo = Integer.parseInt(
                    (listaAmigo.getFirst()).get("id_usuario").toString());

            //Se a relação não existe, a adiciona
            if (sel_amizade(id_usuario, id_amigo).isEmpty()) {
                ins_amizade(id_usuario, id_amigo);
                outputStream.writeBytes("G;1\n");
                sendDisplayFirends();
            } else {
                outputStream.writeBytes("G;0\n");
                sendDisplayFirends();
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Chamada para emviar mensagem de texto de um cliente para outro
    private void sendMsgToClient (String[] msgFields) {
        try {
            if (onlineClients.get(msgFields[2]) != null) {
                DataOutputStream outputStream = new DataOutputStream(onlineClients.get(msgFields[2]).getOutputStream());
                outputStream.writeBytes(msg + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Chamada para enviar todos os clientes online
    private void sendDisplayOnlineClients () {

        String resultado = String.join(";", onlineClients.keySet());

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("B;1;" + resultado + "\n");
        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Chamada para enviar todas as amizades do cliente
    private void sendDisplayFirends () {
        List<Map<String, Object>> infoAmizades = null;
        List<String> amigos = new ArrayList<>();

        try {
            infoAmizades = sel_amizade_usuario(Integer.parseInt(
                    (sel_usuario_username_literal(this.getName()).getFirst()).get("id_usuario").toString()), null);

        } catch (SQLException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        assert infoAmizades != null;

        for (Map<String, Object> mapa : infoAmizades) {
            // Pegando o valor associado à chave "username_amigo"
            amigos.add((String) mapa.get("username_amigo"));
        }

        String resultado = String.join(";", amigos);

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("B;0;" + resultado + "\n");
        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Envia a mensagem de desconexao para o cliente
    private void disconnect() {

        try {
            if (isplaying) {
                DataOutputStream outputStream = new DataOutputStream(oponente.socket.getOutputStream());
                outputStream.writeBytes("C;2;1\n");

                oponente.isplaying = false;
                oponente.tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                oponente.oponente = null;

                isplaying = false;
                tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                oponente = null;

            }

            onlineClients.remove(this.getName());
            threadsOline.remove(this.getName());

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("D\n");

            System.out.println("Disconnecting " + this.getName());


        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Chamada para criar um jogo entre dois cliente
    //Tenta criar o jogo, se deu certo o jogo inicia, se nao envia mensagem de erro
    private void playGame(String[] msgFields){

        for (ThreadServer t : threadsOline.values()) {
            if (t.getName().equals(msgFields[3])) {
                oponente = t;
            }
        }

        if (onlineClients.get(msgFields[3]) != null && !isplaying && !oponente.isplaying){
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;0;1;" + msgFields[3] + ";" + "X" + "\n");

                Thread.sleep(100);

                outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;0;0;" + msgFields[2] + ";" + "O" + "\n");

                oponente.isplaying = true;
                oponente.tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};

                for (ThreadServer t : threadsOline.values()) {
                    if (t.getName().equals(msgFields[2])) {
                        oponente.oponente = t;
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
                if (oponente.isplaying) {
                    outputStream.writeBytes("C;1;1;" + msgFields[3] + "\n");
                } else {
                    outputStream.writeBytes("C;1;0;" + msgFields[3] + "\n");
                }
            } catch (IOException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //Chamada para enviar as jogadas ou se houve um ganhador ou empate, quando o cliente esta jogando
    //Envia a mensagem de acordo com o estado do jogo, se alguem ganhou, empatou ou é uma jogada
    private void sendMoveGame(String[] msgFields) {
        tabuleiro[Integer.parseInt(msgFields[4])] = msgFields[5];

        if (checkWinner(msgFields[5])){
            try {
                System.out.println("Winner " + msgFields[3]);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;1;1;" + msgFields[3] + "\n");

                Thread.sleep(100);

                outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;1;0;" + msgFields[2] + "\n");

                oponente.tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                oponente.isplaying = false;
                oponente.oponente = null;

                tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                isplaying = false;
                oponente = null;
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (isBoardFull()){
            try {
                System.out.println("Draw");
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;2;0\n");

                Thread.sleep(100);

                outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;2;0\n");

                oponente.tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                oponente.isplaying = false;
                oponente.oponente = null;

                tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                isplaying = false;
                oponente = null;
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                DataOutputStream outputStream = new DataOutputStream(onlineClients.get(msgFields[3]).getOutputStream());
                outputStream.writeBytes("C;0;1;" + msgFields[4] + ";" + msgFields[5] + "\n");

                outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes("C;0;0\n");

            } catch (IOException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //Verifica se o "tabuleiro" está cheio, se está cheio deu empate
    private boolean isBoardFull() {
        String[] resultado = Arrays.stream(tabuleiro).filter(s -> !s.equals("-")).toArray(String[]::new);

        return resultado.length > 4;
    }

    //Verifica se o jogador ganhou nesta rodada
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

    //Chamada para desconectar cliente apos uma Exception
    private void disconnectException() {
            if (isplaying) {

                oponente.isplaying = false;
                oponente.tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                oponente.oponente = null;

                isplaying = false;
                tabuleiro = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                oponente = null;

            }
            onlineClients.remove(this.getName());
            threadsOline.remove(this.getName());

            System.out.println("Disconnecting " + this.getName());
    }

}
