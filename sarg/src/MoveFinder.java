import lenz.htw.sarg.Move;
import org.lwjgl.Sys;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MoveFinder {
    private int[][] directions = {
            {+1,  0}, { 0, -1}, {-1, -1},
            {-1,  0}, {0, +1}, { 1, 1},

    };

    private Team ownTeam;
    private BoardManager boardManager;
    private long timeLimit;
    private long timeStartedFindingMove;
    private ExecutorService executor;
    private MoveFinderWorker[] workers;
    private int numOfWorkers = 1;
    private long timeLimitThreshold = 500;

    public MoveFinder(BoardManager boardManager, long timeLimit){
        this.timeLimit = timeLimit;
        this.boardManager = boardManager;

        executor = Executors.newFixedThreadPool(numOfWorkers);
        workers = new MoveFinderWorker[numOfWorkers];
        for (int i = 0; i < numOfWorkers; i++) {
            workers[i] = new MoveFinderWorker(i, this, boardManager);
        }
        //executor.shutdown();
    }

    public Token chooseRandomPiece(){
        Random rand = new Random();
        return ownTeam.belongingTokens.get(rand.nextInt(ownTeam.belongingTokens.size()));
    }

    public Token getBestToken(){
        timeStartedFindingMove = System.currentTimeMillis();
        for (int i = 0; i < numOfWorkers; i++) {
            executor.execute(workers[i]);
        }
        while((System.currentTimeMillis() - timeStartedFindingMove) < timeLimit - timeLimitThreshold){
            //wait
        }
        System.out.println("Choose best token after " + (System.currentTimeMillis() - timeStartedFindingMove) * 1.0/1000.0 + " seconds.");
        return workers[0].bestResult.token;
    }


    public int[][] getCurrentNeighbors(int[] position){
        int[][] neighbors = new int[6][2];
        for(int i = 0; i < directions.length; i++){
            neighbors[i] = new int[]{position[0] + directions[i][0], position[1] + directions[i][1]};
        }
        return neighbors;
    }

    public void setTeam(){
        for (int i = 0; i < numOfWorkers; i++) {
            workers[i].setOwnTeam(boardManager.getOwnTeam());
        }
    }
}
