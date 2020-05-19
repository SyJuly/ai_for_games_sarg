import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import AI.EvaluationParameter;
import AI.MoveFinder;
import Board.BoardManager;
import Board.Token;
import Logging.Logger;
import lenz.htw.sarg.Move;
import lenz.htw.sarg.net.NetworkClient;

public class Main {

    public static void main(String[] args) throws IOException {
        Logger logger = new Logger();

        EvaluationParameter baseEvaluationParams = new EvaluationParameter(0.2,0.6,0.2);

        String teamName = args.length > 0 ? args[0] : "CLIENT J";
        boolean createDumpPlayer = args.length > 1 ? Boolean.getBoolean(args[1]) : false;
        BoardManager boardManager = new BoardManager(logger);


        NetworkClient nc = new NetworkClient("127.0.0.1", teamName, ImageIO.read(new File("/home/july/Projects/AI/logos/earth_bending_emblem_fill_by_mr_droy-d6xo95p.png")));

        long timeLimit = nc.getTimeLimitInSeconds();
        MoveFinder moveFinder = new MoveFinder(boardManager, timeLimit * 1000, createDumpPlayer, baseEvaluationParams);

        nc.getExpectedNetworkLatencyInMilliseconds();

        moveFinder.setTeam(nc.getMyPlayerNumber()); // 0 = rot, 1 = grün, 2 = blau

        do {
            Move receiveMove = nc.receiveMove();
            if (receiveMove == null) {
                Token token = moveFinder.getBestToken();
                Move move = new Move(token.x, token.y);
                System.out.println(teamName + " made Move: " + move.x + "," + move.y);
                nc.sendMove(move);
                moveFinder.prepareNextMoveFinding();
            } else {
                if(receiveMove.x < 0 || receiveMove.y < 0){
                    return;
                }
                System.out.println("Updating with Move: " + receiveMove.x + "," + receiveMove.y);
                boardManager.update(receiveMove);
            }
        } while (!boardManager.isGameOver());
        System.out.println("GAME OVER. Player disconnected.");
        moveFinder.logDepthReport(logger);
        logger.stop();
        System.exit(0);
    }
}