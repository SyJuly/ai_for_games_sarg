package Optimization;

import AI.EvaluationParameter;
import lenz.htw.sarg.Server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class EvolutionaryOptimization {

    private final int NUM_OF_PLAYERS = 3;
    private final int TIME_LIMIT = 3;
    private final int NUM_INDIVIUUMS_PER_GEN = 9;
    private final int GAMEPLAY_ITERATION_PER_GEN = 2;
    private final double MAX_TIME_BONUS = 4;
    private final double MIN_TIME_BONUS = 0;
    private final double MAX_TURNS_TO_TIME_BONUS = 15;

    private EvaluationParameter[] initialParamParents = new EvaluationParameter[]{
        new EvaluationParameter(0.2,0.6,0.2),
        new EvaluationParameter(0.15,0.8,0.5),
        new EvaluationParameter(0.3,0.4,0.3)
    };

    private EvaluationParameter[][] currParamGen;
    private EvaluationParameter[][] prevParamGen;

    private void recombineSelectedParamsToNewGen(EvaluationParameter[] selectedParams){
        Arrays.sort(selectedParams);
        int totalEvaluatedValuesForGen = 0;
        EvaluationParameter[][] newParamGen = new EvaluationParameter[NUM_INDIVIUUMS_PER_GEN/NUM_OF_PLAYERS][NUM_OF_PLAYERS]; // 9 values per generation
        for(int i = 0; i < selectedParams.length; i++){
            totalEvaluatedValuesForGen += selectedParams[i].evaluationValue;
        }

        double activeTokensPercentage = 0;
        for(int p = 0; p < selectedParams.length; p++){
            activeTokensPercentage += selectedParams[p].activeTokensPercentage * (selectedParams[p].evaluationValue/totalEvaluatedValuesForGen);
        }
        double successfulTokensPercentage = 0;
        for(int p = 0; p < selectedParams.length; p++){
            activeTokensPercentage += selectedParams[p].successfulTokensPercentage * (selectedParams[p].successfulTokensPercentage/totalEvaluatedValuesForGen);
        }
        double tokenDistanceToBorderPercentage = 0;
        for(int p = 0; p < selectedParams.length; p++){
            activeTokensPercentage += selectedParams[p].tokenDistanceToBorderPercentage * (selectedParams[p].tokenDistanceToBorderPercentage/totalEvaluatedValuesForGen);
        }

        for(int i = 0; i < newParamGen.length; i++){
            newParamGen[i][0] = selectedParams[i];
            for(int j = 1; j < newParamGen.length; j++){
                newParamGen[i][j] = new EvaluationParameter(activeTokensPercentage, successfulTokensPercentage, tokenDistanceToBorderPercentage);
            }
        }
        prevParamGen = currParamGen; //TODO: needed?
        currParamGen = newParamGen;
    }

    private void evaluateCurrentParamGen(EvaluationParameter[] gameParams, int[] gameResult, int numberOfTurnsToVictory){
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

        for(int j = 0; j < gameParams.length; j++) {
            EvaluationParameter parameter = gameParams[j];
            int gamePointsGainedWithParams = summands[parameter.teamCode];
            int otherPlayer1 = getNextTeam(parameter.teamCode);
            int otherPlayer2 = getNextTeam(otherPlayer1);
            parameter.evaluationValue = gamePointsGainedWithParams - summands[otherPlayer1] - summands[otherPlayer2];
            if (gameResult[parameter.teamCode] == 5) {
                parameter.evaluationValue += timeBonus;
            }
            parameter.evaluationValue += 24; //  adjust range from -24 to 24 in positive area: 0 to 48
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
        /*EvaluationParameter[] nextParamGen = currentParamGen;
        double lowestOfBestParams = Double.NEGATIVE_INFINITY;
        int lowestOfBestParamIndex = 0;
        for (int i = 0; i < currentParamGen.length; i++) {

        }
        for (int i = 0; i < prevParamGen.length; i++) {
            if(prevParamGen[i].evaluationValue > lowestOfBestParams){
                nextParamGen[lowestOfBestParamIndex] = currentParamGen[i];

            }
        }*/


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

        runEvaluationMatch(image, initialParamParents);
        recombineSelectedParamsToNewGen(initialParamParents);

        for(int g = 0; g < currParamGen.length; g++) {
            runEvaluationMatch(image, currParamGen[g]);
        }
        System.exit(0);
    }

    private void runEvaluationMatch(BufferedImage image, EvaluationParameter[] evaluationParameters) {
        for (int n = 0; n < GAMEPLAY_ITERATION_PER_GEN; n++) {
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
                players[i] = new Player(TIME_LIMIT, createDumpPlayer, image, names[i], evaluationParameters[i]);
                playerThreads[i] = new Thread(players[i]);
                playerThreads[i].start();
            }

            int winnerIndex = server.runOnceAndReturnTheWinner(TIME_LIMIT) - 1;
            int[] score = players[0].getCurrentScore();
            int numberOfTurnsToVictory = players[winnerIndex].getTurnNumber();
            evaluateCurrentParamGen(evaluationParameters, score, numberOfTurnsToVictory);
            System.out.println("WINNER IS " + names[winnerIndex] + " - Team " + players[winnerIndex].getTeamCode() + " within " + numberOfTurnsToVictory  + " turns.");
            for (int i = 0; i < evaluationParameters.length; i++) {
                System.out.println(evaluationParameters[i]);
            }
        }
    }

    private void writeGen(){
        for(int g = 0; g < currParamGen.length; g++) {

        }
    }

    public static void main(String[] args) throws IOException {
        new EvolutionaryOptimization().runEvolutionaryOptimization();
    }
}
