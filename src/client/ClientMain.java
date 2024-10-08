package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMain {

    static int port = 12345;
    static String localHost = "127.0.0.1";
    static boolean flag = true;
    static boolean flag_login = true;
    static String name;
    static Socket socket;
    static boolean isPlaying = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        socket = new Socket(localHost, port);

        //ClientThread clientThread = new ClientThread(socket);
        //clientThread.start();

        while (flag_login) {
            System.out.println("Digite seu nome de usuario: ");
            Scanner scanner = new Scanner(System.in);
            name = scanner.nextLine();

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes(name + "\n");

            System.out.println("Digite sua senha: ");
            scanner = new Scanner(System.in);
            String psswrd = scanner.nextLine();

            outputStream.writeBytes(psswrd + "\n");
            Thread.sleep(1500);
        }

        //clientThread.setName(name);


        while (flag) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            String comando = scanner.nextLine();

            switch (comando) {
                case "A":
                    sendMenssageToClient();
                    break;
                case "B":
                    displayOnlineClients();
                    break;
                case "C":
                    challengePlayer();
                    isPlaying = true;
                    break;
                case "D":
                    //clientThread.interrupt();
                    disconnect();
                    flag = false;
                    continue;
                case "Z":
                    break;
                case "help":
                    displayHelp();
                    break;
                default:
                    if (isPlaying) outputStream.writeBytes(comando);
                    else outputStream.writeBytes(comando + "\n");
                    break;
            }
        }
    }

    private static void sendMenssageToClient() {
        try {
            System.out.println("Deseja enviar mensagem para qual jogador? ");
            Scanner scanner = new Scanner(System.in);
            String msgClient = scanner.nextLine();

            System.out.println("Escreva sua mensagem para " + msgClient + ": ");
            scanner = new Scanner(System.in);
            String msgMsg = scanner.nextLine();

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "A;" + name + ";" + msgClient + ";" + msgMsg + "\n");

        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void displayOnlineClients() {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "B\n");

        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void displayHelp(){
        System.out.println("Estes são os comandos para interagir: ");
        System.out.println("A - Enviar menssagem para jogador");
        System.out.println("B - Mostrar jogadores onlines");
        System.out.println("C - Jogar contra outro Jogador");
        System.out.println("D - Desconectar do servidor");
        System.out.println("E - Enviar menssagem para cliente");
        System.out.println("F - Enviar menssagem para cliente");
    }

    private static void disconnect (){
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "D\n");

        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void challengePlayer(){
        try {
            System.out.println("Deseja desafiar qual jogador? ");
            Scanner scanner = new Scanner(System.in);
            String dest = scanner.nextLine();

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "C;"+ name + ";" + dest +"\n");

            boolean end = false;
            while (!end){

            }

        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
