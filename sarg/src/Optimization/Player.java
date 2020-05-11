package Optimization;

import AI.MoveFinder;
import Board.BoardManager;
import Board.Token;
import lenz.htw.sarg.Move;
import lenz.htw.sarg.net.NetworkClient;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Player implements Runnable {

    private final long WAIT_UNTIL_STARTING = 10000;
    private BoardManager boardManager;
    private MoveFinder moveFinder;
    private NetworkClient client;
    private BufferedImage image;
    private boolean isStopped = false;
    private long timeStartedRunning;

    public Player(long timeLimit, boolean createDumpPlayer, BufferedImage image){
        this.image = image;
        boardManager = new BoardManager();
        moveFinder = new MoveFinder(boardManager, timeLimit * 1000, createDumpPlayer);
    }

    public void stop(){
        isStopped = true;
    }

    @Override
    public void run() {
        timeStartedRunning = System.currentTimeMillis();

        while(System.currentTimeMillis() - timeStartedRunning < WAIT_UNTIL_STARTING){
            // wait
        }
        NetworkClient client = new NetworkClient("127.0.0.1", "NC", image);
        boardManager.setTeamCode(client.getMyPlayerNumber());
        moveFinder.setTeam();
        Move lastMove = null;
        try {
            while (!isStopped) {
                System.out.println("Started playing.");
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
                }
            }
        } catch(RuntimeException e){
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("Invalid Move was: " + lastMove.x + "," + lastMove.y);
            System.out.println("Own list of tokens: " + Arrays.toString(boardManager.getOwnTeam().belongingTokens.toArray()));
            System.out.println("Board:");
            Token[][] board = boardManager.getCurrentBoardConfig().board;
            for(int i = 0; i < board.length; i++){
                System.out.println(Arrays.toString(board[i]));
            }

            System.out.println("");
            System.out.println("");
            System.out.println("");
        }
    }
}
