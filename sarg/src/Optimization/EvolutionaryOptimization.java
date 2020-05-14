package Optimization;

import AI.EvaluationParameter;
import lenz.htw.sarg.Server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EvolutionaryOptimization {

    private final int NUM_OF_PLAYERS = 3;
    private final int TIME_LIMIT = 3;
    private final double MAX_TIME_BONUS = 4;
    private final double MIN_TIME_BONUS = 0;
    private final double MAX_TURNS_TO_TIME_BONUS = 15;

    private EvaluationParameter[] currentParamGen = new EvaluationParameter[]{
        new EvaluationParameter(0.2,0.6,0.2),
        new EvaluationParameter(0.15,0.8,0.5),
        new EvaluationParameter(0.3,0.4,0.3)
    };

    private EvaluationParameter[] prevParamGen;

    private void evaluateCurrentParamGen(int[] gameResult, int numberOfTurnsToVictory){
        double timeBonus = getNormedTimeBonus(numberOfTurnsToVictory);
        int[] summands = new int[gameResult.length];
        for(int i = 0; i < gameResult.length; i++){
            int gamepoints = gameResult[i];
            if(gamepoints == 5){
                summands[i] = gamepoints * 4;
            } else{
                summands[i] = gamepoints;
            }
        }

        for(int i = 0; i < currentParamGen.length; i++){
            EvaluationParameter parameter = currentParamGen[i];
            int gamePointsGainedWithParams = summands[parameter.teamCode];
            int otherPlayer1 = getNextTeam(parameter.teamCode);
            int otherPlayer2 = getNextTeam(otherPlayer1);
            parameter.evaluationValue = gamePointsGainedWithParams - summands[otherPlayer1] - summands[otherPlayer2];
            if(gameResult[parameter.teamCode] == 5){
                parameter.evaluationValue += timeBonus;
            }
        }
    }

    private double getNormedTimeBonus(int numberOfTurnsToVictory){
        double timeBonus = Math.max(MAX_TURNS_TO_TIME_BONUS - numberOfTurnsToVictory, 0); // only Bonus if game was won after 15 turns
        return getNormedValue(timeBonus, 0, MAX_TURNS_TO_TIME_BONUS, MIN_TIME_BONUS , MAX_TIME_BONUS);

    }

    private double getNormedValue(double xToNorm, double xMinValue, double xMaxValue, double rangeStart, double rangeEnd){
        return ((rangeEnd - rangeStart) * (xToNorm - xMinValue) / (xMaxValue - xMinValue)) + rangeStart;
    }

    private void selectParamsForNewGen(){
        EvaluationParameter[] nextParamGen = currentParamGen;
        double lowestOfBestParams = Double.NEGATIVE_INFINITY;
        int lowestOfBestParamIndex = 0;
        for (int i = 0; i < currentParamGen.length; i++) {

        }
        for (int i = 0; i < prevParamGen.length; i++) {
            if(prevParamGen[i].evaluationValue > lowestOfBestParams){
                nextParamGen[lowestOfBestParamIndex] = currentParamGen[i];

            }
        }


    }

    public int getNextTeam(int lastTeamCode){
        lastTeamCode++;
        if(lastTeamCode > NUM_OF_PLAYERS - 1){
            return 0;
        }
        return lastTeamCode;
    }

    public void runEvolutionaryOptimization() throws IOException {
        BufferedImage image = ImageIO.read(new File("/home/july/Projects/AI/logos/earth_bending_emblem_fill_by_mr_droy-d6xo95p.png"));

        for(int n = 0; n < 2; n++) {
            Server server = new Server();

            Player[] players = new Player[NUM_OF_PLAYERS];
            Thread[] playerThreads = new Thread[NUM_OF_PLAYERS];
            String[] names = new String[]{"A", "B", "C"};

            long timeStartedRunning = System.currentTimeMillis();
            while (System.currentTimeMillis() - timeStartedRunning < 5000) {
                // wait
            }

            for (int i = 0; i < players.length; i++) {
                boolean createDumpPlayer = i < 1;
                players[i] = new Player(TIME_LIMIT, createDumpPlayer, image, names[i], currentParamGen[i]);
                playerThreads[i] = new Thread(players[i]);
                playerThreads[i].start();
            }

            int winnerIndex = server.runOnceAndReturnTheWinner(TIME_LIMIT) - 1;
            int[] score = players[0].getCurrentScore();
            int numberOfTurnsToVictory = players[winnerIndex].getTurnNumber();
            evaluateCurrentParamGen(score, numberOfTurnsToVictory);
            System.out.println("WINNER IS " + names[winnerIndex] + " within " + numberOfTurnsToVictory);
            for (int i = 0; i < currentParamGen.length; i++) {
               System.out.println(currentParamGen[i]);
            }
        }
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {
        new EvolutionaryOptimization().runEvolutionaryOptimization();
    }
}
