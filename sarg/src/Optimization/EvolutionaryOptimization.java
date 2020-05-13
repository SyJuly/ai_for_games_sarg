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

    private EvaluationParameter baseEvaluationParams = new EvaluationParameter(0.2,0.6,0.2);

    private EvaluationParameter[] currentParamGen;

    private void evaluateCurrentParamGen(int[] gameResult){
        for(int i = 0; i < currentParamGen.length; i++){
            EvaluationParameter parameter = currentParamGen[i];
            int gamePointsGainedWithParams = gameResult[parameter.teamCode];
            int otherPlayer1 = getNextTeam(parameter.teamCode);
            int otherPlayer2 = getNextTeam(otherPlayer1);
            parameter.evaluationValue = (gamePointsGainedWithParams * 2 - gameResult[otherPlayer1] - gameResult[otherPlayer2]) / 10.0;
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
            while (System.currentTimeMillis() - timeStartedRunning < 10000) {
                // wait
            }

            for (int i = 0; i < players.length; i++) {
                boolean createDumpPlayer = i < 1;
                players[i] = new Player(TIME_LIMIT, createDumpPlayer, image, names[i], baseEvaluationParams);
                playerThreads[i] = new Thread(players[i]);
                playerThreads[i].start();
            }

            int winner = server.runOnceAndReturnTheWinner(TIME_LIMIT);
            System.out.println("WINNER IS " + winner);
        }
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {
        new EvolutionaryOptimization().runEvolutionaryOptimization();
    }
}
