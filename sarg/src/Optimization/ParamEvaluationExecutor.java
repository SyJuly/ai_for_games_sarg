package Optimization;

import AI.EvaluationParameter;
import Logging.LogDepthReport;
import Logging.Logger;
import lenz.htw.sarg.Server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ParamEvaluationExecutor {

    private final int TIME_LIMIT = 3;
    private final int TIME_TO_CONNECT_TO_SERVER = 3000;
    private String[] names = new String[]{"A", "B", "C"};

    private int NUM_OF_PLAYERS;
    private int GAMEPLAY_ITERATION_PER_GEN;
    private Logger logger;
    private ParamEvaluator paramEvaluator;
    private BufferedImage image;

    public ParamEvaluationExecutor(Logger logger, int NUM_OF_PLAYERS, int GAMEPLAY_ITERATION_PER_GEN) throws IOException {
        this.logger = logger;
        this.NUM_OF_PLAYERS = NUM_OF_PLAYERS;
        this.GAMEPLAY_ITERATION_PER_GEN = GAMEPLAY_ITERATION_PER_GEN;
        paramEvaluator = new ParamEvaluator(NUM_OF_PLAYERS);
        image = ImageIO.read(new File("/home/july/Projects/AI/logos/earth_bending_emblem_fill_by_mr_droy-d6xo95p.png"));
    }

    public void runMatchWithEvaluation(EvaluationParameter[] evaluationParameters) {
        Double[][] evaluatedValues = new Double[evaluationParameters.length][GAMEPLAY_ITERATION_PER_GEN];
        for (int n = 0; n < GAMEPLAY_ITERATION_PER_GEN; n++) {
            GameResult gameResult = runGame(image, evaluationParameters);
            if(gameResult == null){
                for(int i = 0; i < evaluationParameters.length; i++){
                    evaluatedValues[i][n] = null;
                }
                continue;
            }
            paramEvaluator.evaluateParams(evaluationParameters, gameResult.scoreOfTeams, gameResult.numOfTurnsToGameOver);
            for(int i = 0; i < evaluationParameters.length; i++){
                evaluatedValues[i][n] = evaluationParameters[i].evaluationValue;
            }
            logger.logEvaluation(gameResult.players, evaluationParameters);
        }

        //calculate medium
        for(int i = 0; i < evaluationParameters.length; i++){
            double sumEvaluatedValues = 0;
            int draws = 0;
            for (int n = 0; n < GAMEPLAY_ITERATION_PER_GEN; n++) {
                if(evaluatedValues[i][n] == null){
                    draws++;
                    continue;
                }
                sumEvaluatedValues += evaluatedValues[i][n];
            }
            evaluationParameters[i].evaluationValue = sumEvaluatedValues/ (GAMEPLAY_ITERATION_PER_GEN - draws);

        }
        logger.logMediumEvaluation(evaluatedValues, evaluationParameters);

    }

    private GameResult runGame(BufferedImage image, EvaluationParameter[] evaluationParameters) {
        Server server = new Server();

        Player[] players = new Player[NUM_OF_PLAYERS];
        Thread[] playerThreads = new Thread[NUM_OF_PLAYERS];

        long timeStartedRunning = System.currentTimeMillis();
        while (System.currentTimeMillis() - timeStartedRunning < TIME_TO_CONNECT_TO_SERVER) {
            // wait
        }

        for (int i = 0; i < players.length; i++) {
            boolean createDumpPlayer = i < 1;
            players[i] = new Player(TIME_LIMIT, createDumpPlayer, image, names[i], evaluationParameters[i], logger);
            playerThreads[i] = new Thread(players[i]);
            playerThreads[i].start();
        }

        int winner = server.runOnceAndReturnTheWinner(TIME_LIMIT);

        int[] score = players[0].getCurrentScore();
        int numberOfTurnsToVictory = players[0].getTurnNumber();
        logger.logGameOver(score, winner, players, numberOfTurnsToVictory);
        logger.logDepthReport(new LogDepthReport[]{players[0].getDepthReport(), players[1].getDepthReport(), players[2].getDepthReport()});
        if(winner == -1){
            logger.logDraw();
            return null;
        }
        return new GameResult(players, score, numberOfTurnsToVictory, winner);
    }

}
