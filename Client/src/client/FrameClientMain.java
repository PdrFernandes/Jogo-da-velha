package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrameClientMain extends JFrame{

    String oponente;
    String player = "=";
    static Socket socket;
    ClientThread clientThread;

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
    private JButton jgame3Button;
    private JButton jgame4Button;
    private JButton jgame5Button;
    private JButton jgame0Button;
    private JButton jgame1Button;
    private JButton jgame2Button;
    private JButton jgame6Button;
    private JButton jgame7Button;
    private JButton jgame8Button;
    private JButton jplayButton;
    private JButton jCriarButton;
    private JTextField jUsrAmizadeTextField;
    private JButton jAdicionarButton;
    private JPanel jAmizadePanel;
    private JScrollPane JuserAmizadesScrollPane;
    private JList<String> jlist2;
    private JPanel jgameButtonsPanel;


    public FrameClientMain() {
        jconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonConectarPressed();
            }
        });
        jdesconectarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonDesonectarPressed();
            }
        });
        jloginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonLogarPressed();
            }
        });
        jsendMsgButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonEnviarPressed();
            }
        });
        jrefreshOnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonAtualizarPressed();
            }
        });
        jplayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonJogarPressed();
            }
        });
        jgame0Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jgame0Button.setName("0");
                jgame0Button.setText(player);
                gameButtonPressed(jgame0Button);
            }
        });
        jgame1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jgame1Button.setName("1");
                gameButtonPressed(jgame1Button);
            }
        });
        jgame2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jgame2Button.setName("2");
                gameButtonPressed(jgame2Button);
            }
        });
        jgame3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jgame3Button.setName("3");
                gameButtonPressed(jgame3Button);
            }
        });
        jgame4Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jgame4Button.setName("4");
                gameButtonPressed(jgame4Button);
            }
        });
        jgame5Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jgame5Button.setName("5");
                gameButtonPressed(jgame5Button);
            }
        });
        jgame6Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jgame6Button.setName("6");
                gameButtonPressed(jgame6Button);
            }
        });
        jgame7Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jgame7Button.setName("7");
                gameButtonPressed(jgame7Button);
            }
        });
        jgame8Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jgame8Button.setName("8");
                gameButtonPressed(jgame8Button);
            }
        });
        jCriarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonCriarPressed();
            }
        });
        jAdicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonAdicionarPressed();
            }
        });
    }

    //Comandos quando o botão adicionar é pressionado
    private void buttonAdicionarPressed () {
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                if (Objects.equals(jUsrAmizadeTextField.getText(), "") || Objects.equals(jUsrAmizadeTextField.getText(), null)) {
                    jUsrAmizadeTextField.setText("Digite o nome do jogador");
                } else {
                    outputStream.writeBytes("E;" + jUsrAmizadeTextField.getText() + "\n");
                    jAdicionarButton.setEnabled(false);
                }
            } catch (IOException ex) {
                Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }

    }

    //Comandos quando o botão conectar é pressionado
    private void buttonConectarPressed () {
        try {
            socket = new Socket(jipTextField.getText(), Integer.parseInt(jportTextField.getText()));
            socket.setKeepAlive(true);

            clientThread = new ClientThread(socket, jFrame, jconnectionPanel, jipTextField, jportTextField, jconnectButton,
                    juserDataPanel, jloginButton, jusernameTextField, jpasswordField, jdesconectarButton, jlist1, jusersPanel,
                    jgamePanel, jmsgPanel, jmsgTextArea, jsendMsgTextField, jlist2,jgametextArea, jplayButton, jCriarButton, jAdicionarButton, jUsrAmizadeTextField, jAmizadePanel);

            clientThread.start();

            jconnectionPanel.setVisible(false);
            juserDataPanel.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Comandos quando o botão desconectar é pressionado
    private void buttonDesonectarPressed () {

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "D\n");

        } catch (IOException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Comandos quando o botão logar é pressionado
    private void buttonLogarPressed(){

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("L\n");
            Thread.sleep(500);
            outputStream.writeBytes(jusernameTextField.getText() + "\n");
            Thread.sleep(500);
            outputStream.writeBytes(jpasswordField.getText() + "\n");
            jloginButton.setEnabled(false);
            jCriarButton.setEnabled(false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Comandos quando o botão criar é pressionado
    private void buttonCriarPressed(){

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes("C\n");
            Thread.sleep(500);
            outputStream.writeBytes(jusernameTextField.getText() + "\n");
            Thread.sleep(500);
            outputStream.writeBytes(jpasswordField.getText() + "\n");

            jCriarButton.setEnabled(false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Comandos quando o botão enviar é pressionado
    private void buttonEnviarPressed(){
        if (!jlist1.isSelectionEmpty()) {
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes( "A;" + jusernameTextField.getText() + ";" + jlist1.getSelectedValue() + ";" + jsendMsgTextField.getText() + "\n");

            } catch (IOException ex) {
                Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            jmsgTextArea.append("Selecione um jogador antes de enviar menssagem!\n");
            jmsgTextArea.setCaretPosition(jmsgTextArea.getDocument().getLength() - 1);
        }
    }

    //Comandos quando o botão atualizar é pressionado
    private void buttonAtualizarPressed(){
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "B\n");

        } catch (IOException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Comandos quando o botão jogar é pressionado
    private void buttonJogarPressed(){
        if (!jlist1.isSelectionEmpty()) {
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeBytes( "C;1;" + jusernameTextField.getText() + ";" + jlist1.getSelectedValue() + "\n");

            } catch (IOException ex) {
                Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            jgametextArea.setText("Selecione um jogador antes de jogar!");
        }
    }

    //Comandos quando o botão do jogo (um dos 9 botoes) é pressionado
    private void gameButtonPressed(JButton gameButton){
        String botao = gameButton.getName();
        gameButton.setText(player);
        gameButton.setEnabled(false);

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes( "C;0;" + jusernameTextField.getText() + ";" + oponente + ";" + botao + ";" + player + "\n");
            desativarGameButtons();

        } catch (IOException ex) {
            Logger.getLogger(FrameClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    //Desabilitando algum TextField
    public void desabilitarTextField(JTextField jTextField) {
        jTextField.setEditable(false);
        jTextField.setBackground(Color.GRAY);
    }

    //Desativa os botoes de jogo
    public void desativarGameButtons(){
        jgame0Button.setEnabled(false);
        jgame1Button.setEnabled(false);
        jgame2Button.setEnabled(false);
        jgame3Button.setEnabled(false);
        jgame4Button.setEnabled(false);
        jgame5Button.setEnabled(false);
        jgame6Button.setEnabled(false);
        jgame7Button.setEnabled(false);
        jgame8Button.setEnabled(false);
    }

    //Ativa os botoes de jogo que ainda não foram pressionados
    public void ativarGameButtons(){
        if (Objects.equals(jgame0Button.getText(), "-")) jgame0Button.setEnabled(true);
        if (Objects.equals(jgame1Button.getText(), "-")) jgame1Button.setEnabled(true);
        if (Objects.equals(jgame2Button.getText(), "-")) jgame2Button.setEnabled(true);
        if (Objects.equals(jgame3Button.getText(), "-")) jgame3Button.setEnabled(true);
        if (Objects.equals(jgame4Button.getText(), "-")) jgame4Button.setEnabled(true);
        if (Objects.equals(jgame5Button.getText(), "-")) jgame5Button.setEnabled(true);
        if (Objects.equals(jgame6Button.getText(), "-")) jgame6Button.setEnabled(true);
        if (Objects.equals(jgame7Button.getText(), "-")) jgame7Button.setEnabled(true);
        if (Objects.equals(jgame8Button.getText(), "-")) jgame8Button.setEnabled(true);
    }

    //Reseta os botoes de jogo para o texto "-"
    public void resetGameButtons(){
        jgame0Button.setText("-");
        jgame1Button.setText("-");
        jgame2Button.setText("-");
        jgame3Button.setText("-");
        jgame4Button.setText("-");
        jgame5Button.setText("-");
        jgame6Button.setText("-");
        jgame7Button.setText("-");
        jgame8Button.setText("-");
    }

    //Atualiza o texto do botão que foi passado como String para o texto que foi passado,
    // botao ("0" , "1" .... "8") player ("X" ou "O")
    public void atualizarTextoGameButton(String botao, String player){

        switch (botao) {
            case "0":
                jgame0Button.setEnabled(false);
                jgame0Button.setText(player);
                break;
            case "1":
                jgame1Button.setEnabled(false);
                jgame1Button.setText(player);
                break;
            case "2":
                jgame2Button.setEnabled(false);
                jgame2Button.setText(player);
                break;
            case "3":
                jgame3Button.setEnabled(false);
                jgame3Button.setText(player);
                break;
            case "4":
                jgame4Button.setEnabled(false);
                jgame4Button.setText(player);
                break;
            case "5":
                jgame5Button.setEnabled(false);
                jgame5Button.setText(player);
                break;
            case "6":
                jgame6Button.setEnabled(false);
                jgame6Button.setText(player);
                break;
            case "7":
                jgame7Button.setEnabled(false);
                jgame7Button.setText(player);
                break;
            case "8":
                jgame8Button.setEnabled(false);
                jgame8Button.setText(player);
                break;
        }
    }

    public static void main(String[] args) {
        jFrame = new FrameClientMain();
        jFrame.setContentPane(jFrame.jallPanel);
        jFrame.setTitle("Jogo da velha");
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(800, 800);
        jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jFrame.jdesconectarButton.setVisible(false);
        jFrame.juserDataPanel.setVisible(false);
        jFrame.jgamePanel.setVisible(false);
        jFrame.jmsgPanel.setVisible(false);
        jFrame.jusersPanel.setVisible(false);
        jFrame.jAmizadePanel.setVisible(false);
        jFrame.juserListScrollPane.setViewportView(jFrame.jlist1);
        jFrame.jmsgScrollPane.setViewportView(jFrame.jmsgTextArea);
        jFrame.setVisible(true);
    }
}
