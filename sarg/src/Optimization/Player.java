package Optimization;

import AI.EvaluationParameter;
import AI.MoveFinder;
import Board.BoardManager;
import Board.Token;
import Logging.LogDepthReport;
import Logging.Logger;
import lenz.htw.sarg.Move;
import lenz.htw.sarg.net.NetworkClient;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Player implements Runnable {

    private final long WAIT_UNTIL_STARTING = 5000;
    public String name;
    private LogDepthReport logDepthReport;
    private BoardManager boardManager;
    private MoveFinder moveFinder;
    private int teamCode;
    private BufferedImage image;
    private Logger logger;
    private int[] currentScore;
    private int turnNumber = 0;
    private int receivedMoves = 0;
    private boolean lostPrematurely = false;
    private boolean finished = false;

    public Player(long timeLimit, boolean createDumpPlayer, BufferedImage image, String name, EvaluationParameter params, Logger logger){
        this.image = image;
        this.name = name;
        this.logger = logger;
        boardManager = new BoardManager(logger);
        moveFinder = new MoveFinder(boardManager, timeLimit * 1000, createDumpPlayer, params);
    }

    @Override
    public void run() {
        long timeStartedRunning = System.currentTimeMillis();

        while(System.currentTimeMillis() - timeStartedRunning < WAIT_UNTIL_STARTING){
            // wait
        }
        NetworkClient client = new NetworkClient("127.0.0.1", name, image);
        teamCode = client.getMyPlayerNumber();
        moveFinder.setTeam(teamCode);
        Move lastMove = null;
        try {
            do {
                Move receiveMove = client.receiveMove();

                if (receiveMove == null) {
                    Token token = moveFinder.getBestToken();
                    Move move = new Move(token.x, token.y);
                    lastMove = move;
                    client.sendMove(move);
                    turnNumber++;
                } else {
                    if (receiveMove.x < 0 || receiveMove.y < 0) {
                        if(receiveMove.x < 0 && receiveMove.y < 0){
                            logger.logInvalidMove(teamCode);
                        }
                        continue;
                    }
                    currentScore = boardManager.update(receiveMove);
                    /*for(int i = 0; i < currentScore.length; i++){
                        System.out.println("Team " + i + ": " + currentScore[i]);
                    }*/
                    receivedMoves++;
                    System.out.println("Received move number: " + receivedMoves);
                }
                logDepthReport = moveFinder.getDepthReport();
            } while (!boardManager.isGameOver());
        } catch(RuntimeException e){
            lostPrematurely = true;
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
            logger.logErrror(e);
            throw e;
        }
        finished = true;
        System.out.println("Player performed mic drop.");
    }

    public int[] getCurrentScore(){
        return currentScore;
    }

    public int getTurnNumber(){
        return turnNumber;
    }

    public int getTeamCode(){
        return teamCode;
    }

    public LogDepthReport getDepthReport(){
        return logDepthReport;
    }

    public boolean didLoosePrematurely(){
        return lostPrematurely;
    }

    public boolean isFinished() {
        return finished;
    }
}
