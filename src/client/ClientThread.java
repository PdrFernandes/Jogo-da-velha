package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.*;

import static client.ClientMain.flag;
import static client.ClientMain.flag_login;

// Cuida das menssagens a serem recebidas do servidor
public class ClientThread extends Thread{
    Socket socket;
    String msg = "";

    private FrameClientMain frameClientMain;

    private JPanel jallPanel;
    private JPanel jconnectionPanel;
    private JPanel jmsgPanel;
    private JPanel juserDataPanel;
    private JPanel jusersPanel;
    private JPanel jgamePanel;
    private JTextField jipTextField;
    private JTextField jportTextField;
    private JButton jconnectButton;
    private JButton jdesconectarButton;
    private JPasswordField jpasswordField;
    private JTextField jusernameTextField;
    private JButton jloginButton;
    private JList<String> jList;


    public ClientThread(Socket socket, FrameClientMain frameClientMain, JButton jloginButton, JTextField jusernameTextField,
                        JPasswordField jpasswordField, JButton jdesconectarButton, JList<String> jList) {
        this.socket = socket;
        this.frameClientMain = frameClientMain;
        this.jloginButton = jloginButton;
        this.jusernameTextField = jusernameTextField;
        this.jpasswordField = jpasswordField;
        this.jdesconectarButton = jdesconectarButton;
        this.jList = jList;
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
                    case "E":
                        loginMsg(msgFields);
                        break;
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
        ArrayList<String> usuariosOnline = new ArrayList<>();
        usuariosOnline.addAll(Arrays.asList(msgField));
        usuariosOnline.removeFirst();

        DefaultListModel model = new DefaultListModel();
        this.jList.setModel(model);
        model.clear();

        for(String usuario: usuariosOnline){
            model.addElement(usuario);
        }
    }

    void loginMsg(String[] msgField){
        if (Objects.equals(msgField[1], "1")) {
            flag_login = false;
            this.frameClientMain.desabilitarTextField(jusernameTextField);
            jpasswordField.setEditable(false);
            jpasswordField.setBackground(Color.GRAY);
            System.out.println("Voce realizou o login com sucesso!");
        } else {
            jloginButton.setVisible(true);
            jusernameTextField.setText("Usuario e/ou senha errados. Tente novamente");
            System.out.println("Usuario e/ou senha errados");
        }
    }
}
