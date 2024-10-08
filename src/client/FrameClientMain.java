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
    static boolean flag = true;
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
    }

    private void buttonConectarPressed (ActionEvent event) {
        try {
            socket = new Socket(jipTextField.getText(), Integer.parseInt(jportTextField.getText()));

            clientThread = new ClientThread(socket, jFrame,jloginButton, jusernameTextField, jpasswordField, jdesconectarButton, jlist1);
            clientThread.start();

            desabilitarTextField(jipTextField);
            desabilitarTextField(jportTextField);
            jconnectionPanel.setVisible(false);
            juserDataPanel.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buttonDesonectarPressed (ActionEvent event) {
        clientThread.interrupt();

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "D\n");

        } catch (IOException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

    private void buttonEnviarPressed(ActionEvent event){

    }

    private void buttonAtualizarPressed(ActionEvent event){
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "B\n");

        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
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
        jFrame.setVisible(true);
        jFrame.setSize(600, 400);
        jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jFrame.jdesconectarButton.setVisible(false);
        jFrame.juserDataPanel.setVisible(false);
        jFrame.jgamePanel.setVisible(false);
        jFrame.jmsgPanel.setVisible(false);
        jFrame.jusersPanel.setVisible(false);
    }
}
