package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrameClientMain extends JFrame{

    static int port = 12345;
    static String localHost = "127.0.0.1";
    static boolean desconectou = false;
    static boolean flag_login = true;
    static String name;
    static Socket socket;
    ClientThread clientThread;
    static boolean isPlaying = false;

    private static FrameClientMain jFrame;

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
    private JButton jrefreshOnButton;
    private JList<String> jlist1;
    private JScrollPane juserListScrollPane;
    private JScrollPane jmsgScrollPane;
    private JTextArea jgametextArea;
    private JButton jgame4Button;
    private JButton jgame5Button;
    private JButton jgame6Button;
    private JButton jgame1Button;
    private JButton jgame2Button;
    private JButton jgame3Button;
    private JButton jgame7Button;
    private JButton jgame8Button;
    private JButton jgame9Button;
    private JButton jplayButton;


    public FrameClientMain() {
        jconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonConectarPressed(e);
            }
        });
        jdesconectarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonDesonectarPressed(e);
            }
        });
        jloginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonLogarPressed(e);
            }
        });
        jsendMsgButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonEnviarPressed(e);
            }
        });
        jrefreshOnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonAtualizarPressed(e);
            }
        });
        jplayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonJogarPressed(e);
            }
        });
    }

    //Comandos quando o botão conectar é pressionado
    private void buttonConectarPressed (ActionEvent event) {
        try {
            socket = new Socket(jipTextField.getText(), Integer.parseInt(jportTextField.getText()));

            clientThread = new ClientThread(socket, jFrame, jconnectionPanel, jipTextField, jportTextField, jconnectButton,
                    juserDataPanel, jloginButton, jusernameTextField, jpasswordField, jdesconectarButton, jlist1, jusersPanel, jgamePanel, jmsgPanel, jmsgTextArea, jsendMsgTextField);

            clientThread.start();

            jconnectionPanel.setVisible(false);
            juserDataPanel.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Comandos quando o botão desconectar é pressionado
    private void buttonDesonectarPressed (ActionEvent event) {

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "D\n");

        } catch (IOException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Comandos quando o botão logar é pressionado
    private void buttonLogarPressed(ActionEvent event){

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes(jusernameTextField.getText() + "\n");
            Thread.sleep(1000);
            outputStream.writeBytes(jpasswordField.getText() + "\n");
            jloginButton.setVisible(false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Comandos quando o botão enviar é pressionado
    private void buttonEnviarPressed(ActionEvent event){
        if (!jlist1.isSelectionEmpty()) {
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes( "A;" + jusernameTextField.getText() + ";" + jlist1.getSelectedValue() + ";" + jsendMsgTextField.getText() + "\n");

            } catch (IOException ex) {
                Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            jmsgTextArea.append("Selecione um jogador antes de enviar menssagem!");
            jmsgTextArea.setCaretPosition(jmsgTextArea.getDocument().getLength() - 1);
        }
    }

    //Comandos quando o botão atualizar é pressionado
    private void buttonAtualizarPressed(ActionEvent event){
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "B\n");

        } catch (IOException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buttonJogarPressed(ActionEvent event){
        if (!jlist1.isSelectionEmpty()) {
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes( "C;" + jusernameTextField.getText() + ";" + jlist1.getSelectedValue() + "\n");

            } catch (IOException ex) {
                Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            jgametextArea.setText("Selecione um jogador antes de enviar menssagem!");
        }
    }

    //Desabilitando algum TextField
    public void desabilitarTextField(JTextField jTextField) {
        jTextField.setEditable(false);
        jTextField.setBackground(Color.GRAY);
    }

    public static void main(String[] args) {
        jFrame = new FrameClientMain();
        jFrame.setContentPane(jFrame.jallPanel);
        jFrame.setTitle("Jogo da velha");
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(600, 400);
        jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jFrame.jdesconectarButton.setVisible(false);
        jFrame.juserDataPanel.setVisible(false);
        jFrame.jgamePanel.setVisible(false);
        jFrame.jmsgPanel.setVisible(false);
        jFrame.jusersPanel.setVisible(false);
        jFrame.juserListScrollPane.setViewportView(jFrame.jlist1);
        jFrame.jmsgScrollPane.setViewportView(jFrame.jmsgTextArea);
        jFrame.setVisible(true);
    }
}
