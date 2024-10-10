package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.*;

// Cuida das menssagens a serem recebidas do servidor
public class ClientThread extends Thread{
    Socket socket;
    String msg = "";
    boolean isPlaying = false;
    boolean flag = true;
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
    private JTextArea jmsgTextArea;
    private JTextField jsendMsgTextField;
    private JButton jsendMsgButton;
    private JList<String> jList;

    private JTextArea jgametextArea;
    private JButton jplayButton;
    private JButton jgame4Button;
    private JButton jgame5Button;
    private JButton jgame6Button;
    private JButton jgame1Button;
    private JButton jgame2Button;
    private JButton jgame3Button;
    private JButton jgame7Button;
    private JButton jgame8Button;
    private JButton jgame9Button;

    //O construtor recebe os elementos do JFrame para poder editar eles
    public ClientThread(Socket socket, FrameClientMain frameClientMain, JPanel jconnectionPanel,JTextField jipTextField, JTextField jportTextField, JButton jconnectButton,
                        JPanel juserDataPanel ,JButton jloginButton, JTextField jusernameTextField, JPasswordField jpasswordField, JButton jdesconectarButton,
                        JList<String> jList, JPanel jusersPanel, JPanel jgamePanel, JPanel jmsgPanel, JTextArea jmsgTextArea, JTextField jsendMsgTextField,
                        JTextArea jgametextArea, JButton jplayButton) {
        this.socket = socket;
        this.frameClientMain = frameClientMain;
        this.jconnectionPanel = jconnectionPanel;
        this.jipTextField = jipTextField;
        this.jportTextField = jportTextField;
        this.jconnectButton = jconnectButton;
        this.juserDataPanel = juserDataPanel;
        this.jloginButton = jloginButton;
        this.jusernameTextField = jusernameTextField;
        this.jpasswordField = jpasswordField;
        this.jdesconectarButton = jdesconectarButton;
        this.jList = jList;
        this.jusersPanel = jusersPanel;
        this.jgamePanel = jgamePanel;
        this.jmsgPanel = jmsgPanel;
        this.jmsgTextArea = jmsgTextArea;
        this.jsendMsgTextField = jsendMsgTextField;
        this.jgametextArea = jgametextArea;
        this.jplayButton = jplayButton;

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

                System.out.println(Arrays.toString(msgFields));

                switch (msgFields[0]) {
                    case "A":
                        displayMsgFromClient(msgFields);
                        System.out.println("Mensagem de " + msgFields[1] + ": " + msgFields[3]);
                        break;
                    case "B":
                        displayOnlineClients(msgFields);
                        break;
                    case "C":
                        if (isPlaying) {
                            System.out.println("recebi jogada");
                            recordPlay(msgFields);
                            break;
                        }
                        else if (Objects.equals(msgFields[1], "1")) {
                            refusedPalyGame(msgFields);
                            break;
                        }
                        else {
                            acceptPlayGame(msgFields);
                            break;
                        }
                    case "D":
                        desconectar();
                        return;
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

    private void displayMsgFromClient(String[] msgField){
        String msg = "Mensagem de " + msgField[1] + " :>" + msgField[3];
        jmsgTextArea.append(msg + "\n");
        jmsgTextArea.setCaretPosition(jmsgTextArea.getDocument().getLength() - 1);
    }

    private void displayOnlineClients (String[] msgField){
        ArrayList<String> usuariosOnline = new ArrayList<>(Arrays.asList(msgField));
        usuariosOnline.removeFirst();
        usuariosOnline.remove(jusernameTextField.getText());

        DefaultListModel model = new DefaultListModel();
        this.jList.setModel(model);
        model.clear();

        for(String usuario: usuariosOnline){
            model.addElement(usuario);
        }
    }

    //Chamada quando outro jogador deseja jogar contra, como um handshake,
    //para definir as variaveis nescessárias
    private void acceptPlayGame(String[] msgFields){
        isPlaying = true;
        frameClientMain.oponente = msgFields[3];
        frameClientMain.player = msgFields[4];
        jplayButton.setEnabled(false);
        jgametextArea.setText("Você está jogando contra: " + msgFields[3]);

        if (Objects.equals(msgFields[2], "1")){
            frameClientMain.ativarGameButtons();
            jgametextArea.setText("Sua vez de jogar! ");
        } else {
            frameClientMain.desativarGameButtons();
            jgametextArea.setText("Aguardando o oponente jogar! ");
        }

        //recordPlay(msgFields);
    }

    //Chamada caso um erro ocorra ao jogar contra outro jogador
    // ou ele está ofline, ou já está jogando
    private void refusedPalyGame(String[] msgFields){
        if (Objects.equals(msgFields[2], "1")){
            jgametextArea.setText("Não foi possivel jogar contra " + msgFields[3] + " O jogador já está jogando");
        } else {
            jgametextArea.setText("Não foi possivel jogar contra " + msgFields[3] + " O jogador não está mais online");
        }
    }

    //Chamada para registrar a jogada ou resultado do jogo
    private void recordPlay(String[] msgFields){
        if (Objects.equals(msgFields[1], "1")) {

            if (Objects.equals(msgFields[2], "1")){
                jgametextArea.setText("Parabens, Você ganhou!!!!!!");
            } else {
                jgametextArea.setText("Voce perdeu para o jogador " + msgFields[3]);
            }
            isPlaying = false;
            frameClientMain.resetGameButtons();
            frameClientMain.desativarGameButtons();
            jplayButton.setEnabled(true);

        }
        else if (Objects.equals(msgFields[1], "2")){

            jgametextArea.setText("O jogo empatou!");
            isPlaying = false;
            frameClientMain.resetGameButtons();
            jplayButton.setEnabled(true);

        } else if (Objects.equals(msgFields[2], "1")){

            frameClientMain.atualizarTextoGameButton(msgFields[4], msgFields[5]);
            frameClientMain.ativarGameButtons();
            jgametextArea.setText("Sua vez de jogar! ");

        } else {

            frameClientMain.desativarGameButtons();
            jgametextArea.setText("Aguardando o oponente jogar! ");
        }
    }

    private void loginMsg(String[] msgField){
        if (Objects.equals(msgField[1], "1")) {

            this.frameClientMain.desabilitarTextField(jusernameTextField);
            jpasswordField.setEditable(false);
            jpasswordField.setBackground(Color.GRAY);

            jdesconectarButton.setVisible(true);
            jusersPanel.setVisible(true);
            jgamePanel.setVisible(true);
            jmsgPanel.setVisible(true);
            jusersPanel.setVisible(true);

            this.setName(jusernameTextField.getText());
            System.out.println("Voce realizou o login com sucesso!");
        } else {

            jloginButton.setVisible(true);
            jusernameTextField.setText("Usuario e/ou senha errados. Tente novamente");
            System.out.println("Usuario e/ou senha errados");

        }
    }

    private void desconectar() throws IOException {
        jconnectionPanel.setVisible(true);

        jdesconectarButton.setVisible(false);

        juserDataPanel.setVisible(false);
        jusernameTextField.setEditable(true);
        jusernameTextField.setBackground(Color.WHITE);
        jusernameTextField.setText("");
        jpasswordField.setEditable(true);
        jpasswordField.setBackground(Color.WHITE);
        jpasswordField.setText("");
        jloginButton.setVisible(true);

        jusersPanel.setVisible(false);
        jgamePanel.setVisible(false);
        jmsgPanel.setVisible(false);

        flag = false;
        socket.close();
        interrupt();
    }
}
