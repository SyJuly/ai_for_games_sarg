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

    private EvaluationParameter[] currentParamGen = new EvaluationParameter[]{
        new EvaluationParameter(0.2,0.6,0.2),
        new EvaluationParameter(0.15,0.8,0.5),
        new EvaluationParameter(0.3,0.4,0.3)
    };

    private void evaluateCurrentParamGen(int[] gameResult){
        for(int i = 0; i < currentParamGen.length; i++){
            EvaluationParameter parameter = currentParamGen[i];
            int gamePointsGainedWithParams = gameResult[parameter.teamCode];
            int otherPlayer1 = getNextTeam(parameter.teamCode);
            int otherPlayer2 = getNextTeam(otherPlayer1);
            parameter.evaluationValue = (gamePointsGainedWithParams * 3) - gameResult[otherPlayer1] - gameResult[otherPlayer2];
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
            evaluateCurrentParamGen(score);
            System.out.println("WINNER IS " + names[winnerIndex]);
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
