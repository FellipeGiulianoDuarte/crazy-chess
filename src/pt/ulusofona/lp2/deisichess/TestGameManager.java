package pt.ulusofona.lp2.deisichess;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestGameManager {

    String filePath = "test-files/4.txt";
    File absolutePath = new File(new File(filePath).getAbsolutePath());

    @Test
    public void testLoadGame() {
        GameManager game = new GameManager();
        boolean isGameLoaded = game.loadGame(absolutePath);
        assertTrue(isGameLoaded);
    }

    @Test
    public void testFailedLoadGame(){
        GameManager game = new GameManager();
        boolean isGameLoaded = game.loadGame(new File("naoexisto.txt"));
        assertFalse(isGameLoaded);
    }

    @Test
    public void testWhiteMove() {
        GameManager game = new GameManager();
        boolean isGameLoaded = game.loadGame(absolutePath);
        assertTrue(isGameLoaded);

        //a distância permite mas ainda não é a vez dele de jogar
        boolean blankMove = game.move(1, 2, 1,1);
        assertFalse(blankMove);

        //a distância não permite
        boolean longMove = game.move(1, 2, 3,3);
        assertFalse(longMove);

        //tenta se mover pra cima de outra peça branca
        boolean moveIntoWhitePiece = game.move(1, 2, 1,3);
        assertFalse(moveIntoWhitePiece);
    }

    @Test
    public void testBlackMove(){
        GameManager game = new GameManager();
        boolean isGameLoaded = game.loadGame(absolutePath);
        assertTrue(isGameLoaded);

        //move para espaço vazio
        boolean validMove = game.move(1, 0, 1,1);
        assertTrue(validMove);

        //mexe peça branca, valida que peça preta não se mexe
        boolean firstWhiteMove = game.move(1, 2, 0, 2);
        assertTrue (firstWhiteMove);

        //peça preta captura peça branca
        boolean blackCapture = game.move(1, 1, 0, 2);
        assertTrue (blackCapture);

        //a distância permite mas ainda não é a vez da peça preta jogar
        boolean blackBlankMove = game.move(0, 2, 0, 1);
        assertFalse (blackBlankMove);

        //branco se move
        boolean secondWhiteMove = game.move(1, 3, 1, 2);
        assertTrue(secondWhiteMove);

        //está na vez das pretas jogarem mas o movimento é longo
        boolean blackLongMove = game.move(0, 2, 1, 0);
        assertFalse(blackLongMove);

        //movimento valido das pretas
        boolean blackValidMove = game.move(0, 2, 1, 1);
        assertTrue(blackValidMove);

        //branco se move
        boolean thirdWhiteMove = game.move(1, 2, 0, 2);
        assertTrue(thirdWhiteMove);

        //preto tenta capturar peça preta
        boolean blackCapturesBlack = game.move(1, 1, 2, 1);
        assertFalse(blackCapturesBlack);
    }
}