package Optimization;

import AI.EvaluationParameter;

public class ParamEvaluator {
    private final int MAX_GAMEPOINTS = 5;
    private final int VICTORY_MODIFIER = 4;
    private final double MAX_TIME_BONUS = 4;
    private final double MIN_TIME_BONUS = 0;
    private final double MAX_TURNS_TO_TIME_BONUS = 15;
    private int NUM_OF_PLAYERS;

    public ParamEvaluator(int NUM_OF_PLAYERS){
        this.NUM_OF_PLAYERS = NUM_OF_PLAYERS;
    }

    public void evaluateParams(EvaluationParameter[] gameParams, GameResult gameResult){
        int[] gameResultScore = gameResult.scoreOfTeams;
        double timeBonus = getNormedTimeBonus(gameResult.numOfTurnsToGameOver);
        int[] summands = new int[gameResultScore.length];
        for(int i = 0; i < gameResultScore.length; i++){
            int gamepoints = gameResultScore[i];
            if(gamepoints >= MAX_GAMEPOINTS){
                summands[i] = MAX_GAMEPOINTS * VICTORY_MODIFIER;
            } else {
                summands[i] = gamepoints;
            }
        }

        for(int j = 0; j < gameParams.length; j++) {
            EvaluationParameter parameter = gameParams[j];
            int gamePointsGainedWithParams = summands[parameter.teamCode];
            if(gameResult.teamsLostPrematurely[parameter.teamCode]){
                gamePointsGainedWithParams = 0; //penality for loosing early
            }
            int otherPlayer1 = getNextTeam(parameter.teamCode);
            int otherPlayer2 = getNextTeam(otherPlayer1);

            parameter.evaluationValue = gamePointsGainedWithParams - summands[otherPlayer1] - summands[otherPlayer2];

            if (gameResultScore[parameter.teamCode] >= MAX_GAMEPOINTS) {
                parameter.evaluationValue += timeBonus;
            }
            parameter.evaluationValue += 24; //  adjust range from -24 to 24 in positive area: 0 to 48
        }
    }

    public int getNextTeam(int lastTeamCode){
        lastTeamCode++;
        if(lastTeamCode > NUM_OF_PLAYERS - 1){
            return 0;
        }
        return lastTeamCode;
    }

    private double getNormedTimeBonus(int numberOfTurnsToVictory){
        double timeBonus = Math.max(MAX_TURNS_TO_TIME_BONUS - numberOfTurnsToVictory, 0); // only Bonus if game was won after 15 turns
        return getNormedValue(timeBonus, 0, MAX_TURNS_TO_TIME_BONUS, MIN_TIME_BONUS , MAX_TIME_BONUS);

    }

    private double getNormedValue(double xToNorm, double xMinValue, double xMaxValue, double rangeStart, double rangeEnd){
        return ((rangeEnd - rangeStart) * (xToNorm - xMinValue) / (xMaxValue - xMinValue)) + rangeStart;
    }
}
