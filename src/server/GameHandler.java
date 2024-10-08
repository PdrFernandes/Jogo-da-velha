package server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameHandler{
    private Socket player1;
    private Socket player2;
    private char[] board;
    private char currentPlayer;

    public GameHandler(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;

        board = new char[9];
        for (int i = 0; i < 9; i++) {
            board[i] = '-';
        }
        currentPlayer = 'X';
    }

    public void innit() {
        try {
            DataOutputStream out1 = new DataOutputStream(player1.getOutputStream());
            DataInputStream in1 = new DataInputStream(player1.getInputStream());

            DataOutputStream out2 = new DataOutputStream(player2.getOutputStream());
            DataInputStream in2 = new DataInputStream(player2.getInputStream());

            out1.writeBytes("Voce e o jogador X. Faca sua jogada.\n");
            out2.writeBytes("Voce e o jogador O. Aguarde a jogada do jogador X.\n");

            boolean gameEnded = false;

            while (!gameEnded) {
                if (currentPlayer == 'X') {
                    out1.writeBytes("Sua vez de jogar.\n");
                    int move = in1.readInt();
                    System.out.println(move);
                    if (makeMove(move, 'X')) {
                        out2.writeBytes("O jogador X jogou na posicao " + move + "\n");
                        gameEnded = checkGameState(out1, out2);
                        currentPlayer = 'O';
                    } else {
                        out1.writeUTF("Movimento inválido. Tente novamente.\n");
                    }
                } else {
                    out2.writeBytes("Sua vez de jogar.\n");
                    int move = in2.readInt();
                    if (makeMove(move, 'O')) {
                        out1.writeBytes("O jogador O jogou na posicao " + move + "\n");
                        gameEnded = checkGameState(out2, out1);
                        currentPlayer = 'X';
                    } else {
                        out2.writeBytes("Movimento inválido. Tente novamente.\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Realiza uma jogada
    private boolean makeMove(int position, char player) {
        if (position < 0 || position > 8 || board[position] != '-') {
            return false;
        }
        board[position] = player;
        return true;
    }

    // Verifica se o jogo terminou
    private boolean checkGameState(DataOutputStream currentOut, DataOutputStream opponentOut) throws IOException {
        if (checkWinner()) {
            currentOut.writeBytes("Você venceu!\n");
            opponentOut.writeBytes("Você perdeu!\n");
            return true;
        } else if (isBoardFull()) {
            currentOut.writeBytes("Empate!\n");
            opponentOut.writeBytes("Empate!\n");
            return true;
        }
        return false;
    }

    // Verifica se há um vencedor
    private boolean checkWinner() {
        int[][] winningPositions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Linhas
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Colunas
                {0, 4, 8}, {2, 4, 6}             // Diagonais
        };

        for (int[] wp : winningPositions) {
            if (board[wp[0]] == currentPlayer && board[wp[1]] == currentPlayer && board[wp[2]] == currentPlayer) {
                return true;
            }
        }
        return false;
    }

    // Verifica se o tabuleiro está cheio (empate)
    private boolean isBoardFull() {
        for (char c : board) {
            if (c == '-') {
                return false;
            }
        }
        return true;
    }
}
