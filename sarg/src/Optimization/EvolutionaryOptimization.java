package Optimization;

import AI.EvaluationParameter;
import Logging.Logger;
import lenz.htw.sarg.Server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class EvolutionaryOptimization {

    private final int NUM_OF_PLAYERS = 3;
    private final int TIME_LIMIT = 3;
    private final int NUM_INDIVIUUMS_PER_GEN = 9;
    private final int GAMEPLAY_ITERATION_PER_GEN = 2;
    private final int MAX_GAMEPOINTS = 5;
    private final int VICTORY_MODIFIER = 4;
    private final double MAX_TIME_BONUS = 4;
    private final double MIN_TIME_BONUS = 0;
    private final double MAX_TURNS_TO_TIME_BONUS = 15;
    private final int HIGH_MUTATION_MAX = 1000;
    private final int LOW_MUTATION_MAX = 5000;
    private Logger logger;
    private Random random;
    private EvaluationParamValidator validator;

    private EvaluationParameter[] initialParamParents = new EvaluationParameter[]{
        new EvaluationParameter(0.2,0.6,0.2),
        new EvaluationParameter(0.15,0.8,0.5),
        new EvaluationParameter(0.3,0.4,0.3)
    };

    private EvaluationParameter[][] currParamGen;
    private EvaluationParameter[] selectedParams;

    private void recombineAndMutateSelectedParamsToNewGen(EvaluationParameter[] parentParams){
        Arrays.sort(parentParams);
        int totalEvaluatedValuesForGen = 0;
        double[][] high_Mutations = new double[][]{getMutation(HIGH_MUTATION_MAX), getMutation(HIGH_MUTATION_MAX), getMutation(HIGH_MUTATION_MAX)};
        double[][] low_Mutations = new double[][]{new double[]{0,0,0}, getMutation(LOW_MUTATION_MAX), getMutation(LOW_MUTATION_MAX)};
        logger.logMutation(high_Mutations, low_Mutations);

        EvaluationParameter[][] newParamGen = new EvaluationParameter[NUM_INDIVIUUMS_PER_GEN/NUM_OF_PLAYERS][NUM_OF_PLAYERS]; // 9 values per generation
        for(int i = 0; i < parentParams.length; i++){
            totalEvaluatedValuesForGen += parentParams[i].evaluationValue;
        }


        double activeTokensPercentage = 0;
        double successfulTokensPercentage = 0;
        double tokenDistanceToBorderPercentage = 0;
        for(int p = 0; p < parentParams.length; p++){
            double base = parentParams[p].evaluationValue/totalEvaluatedValuesForGen;
            activeTokensPercentage += parentParams[p].activeTokensPercentage * base;
            successfulTokensPercentage += parentParams[p].successfulTokensPercentage * base;
            tokenDistanceToBorderPercentage += parentParams[p].tokenDistanceToBorderPercentage * base;
        }

        for(int i = 0; i < newParamGen.length; i++){
            newParamGen[i][0] = parentParams[i]; // first has no mutation
            for(int j = 1; j < newParamGen.length; j++){
                double[][] mutations = j == newParamGen.length -1 ? high_Mutations : low_Mutations;
                newParamGen[i][j] = validator.getValidatedMutatedParams(activeTokensPercentage, successfulTokensPercentage, tokenDistanceToBorderPercentage, mutations[i]);
            }
        }
        currParamGen = newParamGen;
        logger.logRecombination(currParamGen);
    }


    private double[] getMutation(int max){
        int activeTokensModifier = random.nextInt(max*2) - max;
        int successfulTokensModifier = random.nextInt(max*2) - max;
        int tokenDistanceToBorderModifier = - activeTokensModifier - successfulTokensModifier;
        return new double[]{activeTokensModifier/(10.0 * max), successfulTokensModifier/(10.0 * max), tokenDistanceToBorderModifier/(10.0 * max)};
    }

    private void evaluateParams(EvaluationParameter[] gameParams, int[] gameResult, int numberOfTurnsToVictory){
        double timeBonus = getNormedTimeBonus(numberOfTurnsToVictory);
        int[] summands = new int[gameResult.length];
        for(int i = 0; i < gameResult.length; i++){
            int gamepoints = gameResult[i];
            if(gamepoints >= MAX_GAMEPOINTS){
                summands[i] = MAX_GAMEPOINTS * VICTORY_MODIFIER;
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

    public int getNextTeam(int lastTeamCode){
        lastTeamCode++;
        if(lastTeamCode > NUM_OF_PLAYERS - 1){
            return 0;
        }
        return lastTeamCode;
    }
    private void runMatchWithEvaluation(BufferedImage image, EvaluationParameter[] evaluationParameters) {
        Double[][] evaluatedValues = new Double[evaluationParameters.length][GAMEPLAY_ITERATION_PER_GEN];
        for (int n = 0; n < GAMEPLAY_ITERATION_PER_GEN; n++) {
            GameResult gameResult = runGame(image, evaluationParameters);
            if(gameResult == null){
                for(int i = 0; i < evaluationParameters.length; i++){
                    evaluatedValues[i][n] = null;
                }
                continue;
            }
            evaluateParams(evaluationParameters, gameResult.scoreOfTeams, gameResult.numOfTurnsToGameOver);
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

        int winner = server.runOnceAndReturnTheWinner(TIME_LIMIT);

        int[] score = players[0].getCurrentScore();
        int numberOfTurnsToVictory = players[0].getTurnNumber();
        logger.logGameOver(score, winner, players, numberOfTurnsToVictory);
        if(winner == -1){
            logger.logDraw();
            return null;
        }
        return new GameResult(players, score, numberOfTurnsToVictory, winner);
    }

    public void runEvolutionaryOptimization() throws IOException {
        BufferedImage image = ImageIO.read(new File("/home/july/Projects/AI/logos/earth_bending_emblem_fill_by_mr_droy-d6xo95p.png"));
        logger = new Logger();
        validator = new EvaluationParamValidator(logger);
        random = new Random(1);

        runMatchWithEvaluation(image, initialParamParents);
        recombineAndMutateSelectedParamsToNewGen(initialParamParents);

        logger.logNewGeneration(0);
        for(int g = 0; g < currParamGen.length; g++) {
            runMatchWithEvaluation(image, currParamGen[g]);
        }
        selectParentParams(image);
        recombineAndMutateSelectedParamsToNewGen(selectedParams);
        for(int g = 0; g < currParamGen.length; g++) {
            runMatchWithEvaluation(image, currParamGen[g]);
        }

        logger.stop();
        System.exit(0);
    }

    private void selectParentParams(BufferedImage image) {
        EvaluationParameter[] matchChampions = getMediumMatchChampionsOfCurrentParamGen();
        runMatchWithEvaluation(image, matchChampions);
        selectedParams = matchChampions;
    }

    private EvaluationParameter[] getMediumMatchChampionsOfCurrentParamGen() {
        EvaluationParameter[] champions = new EvaluationParameter[currParamGen.length];
        for(int c = 0; c < currParamGen.length; c++) {
            champions[c] = currParamGen[c][0];
            for(int r = 1; r < currParamGen[c].length; r++) {
                if(currParamGen[c][r].evaluationValue > champions[c].evaluationValue){
                    champions[c] = currParamGen[c][r];
                }
            }
        }
        return champions;
    }

    public static void main(String[] args) throws IOException {
        new EvolutionaryOptimization().runEvolutionaryOptimization();
    }
}
