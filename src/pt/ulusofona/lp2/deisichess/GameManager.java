package pt.ulusofona.lp2.deisichess;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GameManager {


    //guarda dados do tabuleiro
    private int[][] board;

    //guarda tamanho do tabuleiro
    private int boardSize;

    //guarda informações das peças
    private List<String[]> piecesData;

    //quantos movimentos foram feitos
    private int moveCount;

    //contador para rastrear empates
    private int drawCounter;

    //Reis pretos no jogo
    private int blackKingCount;

    //Reis brancos no jogo
    private int whiteKingCount;

    //Jogo pode acabar por exaustao
    private boolean gameCanEndByExaustion;

    //Contadores das estatísticas
    private int blackCaptures;
    private int blackValidMoves;
    private int blackInvalidMoves;
    private int whiteCaptures;
    private int whiteValidMoves;
    private int whiteInvalidMoves;

    public GameManager() {
        this.board = null;
        this.boardSize = 0;
        this.piecesData = new ArrayList<>();
        this.moveCount = 0;
        this.drawCounter = 0;
        this.blackKingCount = 0;
        this.whiteKingCount = 0;
        this.gameCanEndByExaustion = false;
        this.blackCaptures = 0;
        this.blackValidMoves = 0;
        this.blackInvalidMoves = 0;
        this.whiteCaptures = 0;
        this.whiteValidMoves = 0;
        this.whiteInvalidMoves = 0;
    }

    public boolean loadGame(File file) {
        try {
            Scanner scanner = new Scanner(file);
            if (scanner.hasNextLine()) {
                boardSize = Integer.parseInt(scanner.nextLine());
            }
            if (scanner.hasNextLine()) {
                int numPieces = Integer.parseInt(scanner.nextLine());
                for (int i = 0; i < numPieces; i++) {
                    if (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] data = line.split(":");
                        String[] png = Arrays.copyOf(data, data.length + 1);

                        //adiciona imagem
                        if (Integer.parseInt(png[2]) == 0) {
                            png[4] = "crazy_emoji_black.png";
                            piecesData.add(png);
                        } else {
                            png[4] = "crazy_emoji_white.png";
                            piecesData.add(png);
                        }
                    }
                }
            }
            board = new int[boardSize][boardSize];
            int row = 0;
            while (scanner.hasNextLine()) {
                String[] lineData = scanner.nextLine().split(":");
                for (int col = 0; col < lineData.length; col++) {
                    board[row][col] = Integer.parseInt(lineData[col]);
                }
                row++;
            }
            scanner.close();

            return true;
        } catch (FileNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public boolean move(int x0, int y0, int x1, int y1) {

        int currTeam = getCurrentTeamID();
        String[] currSquare = getSquareInfo(x0, y0);
        String[] nextSquare = getSquareInfo(x1, y1);

        //Verifica se a origem está vazia
        if (currSquare.length == 0) {

            if (currTeam == 0) {
                //jogada invalida pretas
                this.blackInvalidMoves++;
            } else {
                this.whiteInvalidMoves++;
            }
            return false;
        }

        //Verifica se aquela peça pode se mexer
        if (Integer.parseInt(currSquare[2]) == currTeam) {

            //não da um passo maior que a perna
            if ((Math.abs(x1 - x0) <= 1) && (Math.abs(y1 - y0) <= 1)) {

                int indexCurrSquare = Integer.parseInt(currSquare[0]);

                // Verifica se o destino está vazio
                if (nextSquare.length == 0) {
                    board[y1][x1] = indexCurrSquare;
                    board[y0][x0] = 0;

                    //estatisticas
                    if (currTeam == 0) {
                        //jogada valida pretas sem captura
                        this.blackValidMoves++;
                    } else {
                        this.whiteValidMoves++;
                    }

                    //não teve capturas
                    if(gameCanEndByExaustion){
                        this.drawCounter++;
                    }

                    // Incrementa o contador de movimentos
                    this.moveCount++;
                    return true;
                }

                //verifica se é do mesmo time
                if (Objects.equals(currSquare[2], nextSquare[2])) {
                    return false;
                } else {
                    //peça existe
                    board[y1][x1] = indexCurrSquare;
                    board[y0][x0] = 0;

                    //estatisticas
                    if (currTeam == 0) {
                        //jogada valida pretas com capture
                        this.blackValidMoves++;
                        this.blackCaptures++;
                    } else {
                        this.whiteValidMoves++;
                        this.whiteCaptures++;
                    }

                    //captura de peça é condicão para jogo terminar por exaustão
                    this.gameCanEndByExaustion = true;

                    // Incrementa o contador de movimentos
                    this.moveCount++;
                    return true;
                }
            } else {

                if (currTeam == 0) {
                    //jogada invalida pretas
                    this.blackInvalidMoves++;
                } else {
                    this.whiteInvalidMoves++;
                }
                return false;
            }

        } else {

            if (currTeam == 0) {
                //jogada invalida pretas
                this.blackInvalidMoves++;
            } else {
                this.whiteInvalidMoves++;
            }
            return false;
        }

    }

    public String[] getSquareInfo(int x, int y) {
        for (String[] piece : piecesData) {
            int index = Integer.parseInt(piece[0]);
            String[] pieceInfo = getPieceInfo(index);
            if (Objects.equals(pieceInfo[4], "capturado")) {
                continue;
            }
            if (Integer.parseInt(pieceInfo[5]) == x && Integer.parseInt(pieceInfo[6]) == y) {
                return piece;
            }
        }
        // Retorna um array vazio se nenhum quadrado com as coordenadas especificadas contiver uma peça
        return new String[0];
    }

    public String[] getPieceInfo(int ID) {
        for (String[] piece : piecesData) {
            if (Integer.parseInt(piece[0]) == ID) {
                String[] pieceInfo = new String[7]; // Novo array com tamanho 7
                pieceInfo[0] = piece[0]; // ID
                pieceInfo[1] = piece[1]; // Tipo
                pieceInfo[2] = piece[2]; // Equipe
                pieceInfo[3] = piece[3]; // Alcunha
                pieceInfo[4] = ""; // Estado inicialmente vazio
                pieceInfo[5] = ""; // Posição x inicialmente vazia
                pieceInfo[6] = ""; // Posição y inicialmente vazia

                String[] position = getPositionByID(ID);
                if (position.length > 0) {
                    pieceInfo[4] = "em jogo";
                    pieceInfo[5] = position[0];
                    pieceInfo[6] = position[1];
                } else {
                    pieceInfo[4] = "capturado";
                }
                return pieceInfo;
            }
        }
        return null;
    }

    public String[] getPositionByID(int ID) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == ID) {
                    return new String[]{String.valueOf(j), String.valueOf(i)};
                }
            }
        }
        return new String[0]; // Retorna um array vazio se nenhuma peça com o ID especificado for encontrada
    }

    public String getPieceInfoAsString(int ID) {
        String[] pieceInfo = getPieceInfo(ID);
        if (Objects.equals(pieceInfo[4], "capturado")) {
            return pieceInfo[0] + " | " + pieceInfo[1] + " | " + pieceInfo[2] + " | " + pieceInfo[3] + " @ (n/a)";
        } else {
            return pieceInfo[0] + " | " + pieceInfo[1] + " | " + pieceInfo[2] + " | " + pieceInfo[3]
                    + " @ (" + pieceInfo[5] + ", " + pieceInfo[6] + ")";
        }
    }

    public int getCurrentTeamID() {
        return (moveCount % 2);
    }

    public boolean gameOver() {
        this.blackKingCount = 0;
        this.whiteKingCount = 0;
        for (String[] piece : piecesData) {
            String[] pieceInfo = getPieceInfo(Integer.parseInt(piece[0]));
            if (pieceInfo[1].equals("0") && pieceInfo[4].equals("em jogo")) { // Verifica se a peça é um rei e está em jogo
                if (pieceInfo[2].equals("0")) { // Verifica se o rei é do time preto
                    blackKingCount++;
                } else {
                    whiteKingCount++;
                }
            }
        }

        // Verifica as condições para determinar se o jogo acabou
        if (this.blackKingCount == 0 || this.whiteKingCount == 0) {
            return true; // Retorna true se um dos times não tiver mais reis
        } else if (this.blackKingCount == 1 && this.whiteKingCount == 1) {
            return true; // Retorna true se houver apenas um rei de cada equipe restante
        } else {
            if (gameCanEndByExaustion) { //Jogo pode ser encerrado por exaustão
                return this.drawCounter >= 10; // Retorna true se houve 10 ou mais movimentos sem captura
            } else {
                return false; // Retorna false se nenhuma das condições acima for atendida
            }
        }
    }

    public float retonaTeste(float valor) {
        return Math.min(1, Math.max(0, valor));
    }

    public ArrayList<String> getGameResults() {
        ArrayList<String> results = new ArrayList<>();

        results.add("JOGO DE CRAZY CHESS");
        if (blackKingCount == 1 && whiteKingCount == 1) {
            results.add("Resultado: EMPATE");
        } else if (blackKingCount == 0) {
            results.add("Resultado: VENCERAM AS BRANCAS");
        } else if (whiteKingCount == 0) {
            results.add("Resultado: VENCERAM AS PRETAS");
        } else{
            results.add("Resultado: EMPATE");
        }

        // Informações sobre as equipes
        results.add("---");
        results.add("Equipa das Pretas");
        results.add(String.valueOf(blackCaptures));
        results.add(String.valueOf(blackValidMoves));
        results.add(String.valueOf(blackInvalidMoves));

        results.add("Equipa das Brancas");
        results.add(String.valueOf(whiteCaptures));
        results.add(String.valueOf(whiteValidMoves));
        results.add(String.valueOf(whiteInvalidMoves));

        return results;
    }

    public JPanel getAuthorsPanel() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Autores: [Nome do Autor 1], [Nome do Autor 2]");
        panel.add(label);
        return panel;
    }
}
