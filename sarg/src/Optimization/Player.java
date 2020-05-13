package Optimization;

import AI.EvaluationParameter;
import AI.MoveFinder;
import Board.BoardManager;
import Board.Token;
import lenz.htw.sarg.Move;
import lenz.htw.sarg.net.NetworkClient;
import org.lwjgl.Sys;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Player implements Runnable {

    private final long WAIT_UNTIL_STARTING = 5000;
    private String name;
    private BoardManager boardManager;
    private MoveFinder moveFinder;
    private NetworkClient client;
    private BufferedImage image;
    private long timeStartedRunning;

    public Player(long timeLimit, boolean createDumpPlayer, BufferedImage image, String name, EvaluationParameter params){
        this.image = image;
        this.name = name;
        boardManager = new BoardManager();
        moveFinder = new MoveFinder(boardManager, timeLimit * 1000, createDumpPlayer, params);
    }

    @Override
    public void run() {
        timeStartedRunning = System.currentTimeMillis();

        while(System.currentTimeMillis() - timeStartedRunning < WAIT_UNTIL_STARTING){
            // wait
        }
        NetworkClient client = new NetworkClient("127.0.0.1", name, image);
        moveFinder.setTeam(client.getMyPlayerNumber());
        Move lastMove = null;
        try {
            do {
                Move receiveMove = client.receiveMove();
                if (receiveMove == null) {
                    Token token = moveFinder.getBestToken();
                    Move move = new Move(token.x, token.y);
                    lastMove = move;
                    client.sendMove(move);
                } else {
                    if (receiveMove.x < 0 || receiveMove.y < 0) {
                        return;
                    }
                    boardManager.update(receiveMove);
                    /*if(name.equals("C") && lastMove != null && receiveMove.x == lastMove.x && receiveMove.y == lastMove.y) {
                        System.out.println("");
                        System.out.println("");
                        System.out.println("");
                        System.out.println("Team " + client.getMyPlayerNumber() + " chose VALID Move: " + lastMove.x + "," + lastMove.y);
                        System.out.println("Own list of tokens: " + Arrays.toString(boardManager.getCurrentBoardConfig().getCurrentTokensOfTeam(client.getMyPlayerNumber()).toArray()));
                        System.out.println("Board:");
                        Token[][] board = boardManager.getCurrentBoardConfig().board;
                        for (int i = 0; i < board.length; i++) {
                            System.out.println(Arrays.toString(board[i]));
                        }

                        System.out.println("");
                        System.out.println("");
                        System.out.println("");
                    }*/
                }
            } while (!boardManager.isGameOver());
        } catch(RuntimeException e){
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("Player " + name + "/ Team " + client.getMyPlayerNumber()+ " chose invalid Move: " + lastMove.x + "," + lastMove.y);
            System.out.println("Own list of tokens: " + Arrays.toString(boardManager.getCurrentBoardConfig().getCurrentTokensOfTeam(client.getMyPlayerNumber()).toArray()));
            System.out.println("Board:");
            Token[][] board = boardManager.getCurrentBoardConfig().board;
            for(int i = 0; i < board.length; i++){
                System.out.println(Arrays.toString(board[i]));
            }

            System.out.println("");
            System.out.println("");
            System.out.println("");
            throw e;
        }
        System.out.println("Player performed mic drop.");
        //System.exit(0);
    }
}
