package AI;

import Board.BoardManager;
import Board.Token;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MoveFinder {
    private int[][] directions = {
            {+1,  0}, { 0, -1}, {-1, -1},
            {-1,  0}, {0, +1}, { 1, 1},

    };

    private boolean isDumpPlayer;
    private int ownTeamCode;
    private BoardManager boardManager;
    private long timeLimit;
    private long timeStartedFindingMove;
    private ExecutorService executor;
    private MoveFinderWorker[] workers;
    private AlphaBetaResult[] results;
    private int[] depths = new int[]{7, 9, 10};
    private long timeLimitThreshold = 500;

    public MoveFinder(BoardManager boardManager, long timeLimit, boolean createDumpPlayer){
        this.timeLimit = timeLimit;
        this.boardManager = boardManager;
        this.isDumpPlayer = createDumpPlayer;

        if(!isDumpPlayer){
            setUpMoveFinderWorker();
        }
    }

    private void setUpMoveFinderWorker() {
        int numOfWorkers = depths.length;
        executor = Executors.newFixedThreadPool(numOfWorkers);
        workers = new MoveFinderWorker[numOfWorkers];
        results = new AlphaBetaResult[numOfWorkers];
        for (int i = 0; i < numOfWorkers; i++) {
            workers[i] = new MoveFinderWorker(i, results, boardManager, depths[i]);
        }
    }

    public Token chooseRandomPiece(){
        Random rand = new Random();
        List<Token> activeTokens = boardManager.getCurrentBoardConfig().getCurrentTokensOfTeam(ownTeamCode);
        return activeTokens.get(rand.nextInt(activeTokens.size()));
    }


    public Token getBestToken(){
        if(boardManager.getCurrentBoardConfig().getCurrentTokensOfTeam(ownTeamCode).size() < 1){
            return new Token(-1,-1, ownTeamCode);
        }
        if(isDumpPlayer){
            return chooseRandomPiece();
        }
        timeStartedFindingMove = System.currentTimeMillis();
        Future<AlphaBetaResult>[] results = new Future[depths.length];
        for (int i = 0; i < depths.length; i++) {
            results[i] = executor.submit(workers[i]);
        }
        while((System.currentTimeMillis() - timeStartedFindingMove) < timeLimit - timeLimitThreshold){
            //wait
        }
        return chooseBestToken(results);
    }

    private Token chooseBestToken(Future<AlphaBetaResult>[] results) {
        for (int i = depths.length -1; i >= 0; i--) {
            if(results[i].isDone()){
                stopFindingBestToken();
                //System.out.println("Choose best token after " + (System.currentTimeMillis() - timeStartedFindingMove) * 1.0/1000.0 + " seconds with depth: " + depths[i]);
                return this.results[i].token;
            }
        }
        System.out.println("WARNING: Chose random token as fall back.");
        return chooseRandomPiece();
    }

    private void stopFindingBestToken(){
        for (int i = 0; i < depths.length; i++) {
            workers[i].cancelTask();
        }
    }


    public int[][] getCurrentNeighbors(int[] position){
        int[][] neighbors = new int[6][2];
        for(int i = 0; i < directions.length; i++){
            neighbors[i] = new int[]{position[0] + directions[i][0], position[1] + directions[i][1]};
        }
        return neighbors;
    }

    public void setTeam(int teamCode){
        ownTeamCode = teamCode;
        if (isDumpPlayer){
            return;
        }
        for (int i = 0; i < depths.length; i++) {
            workers[i].setOwnTeam(teamCode);
        }
    }
}
