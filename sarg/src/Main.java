import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lenz.htw.sarg.Move;
import lenz.htw.sarg.net.NetworkClient;

public class Main {

    public static void main(String[] args) throws IOException {

        String teamName = args.length > 0 ? args[0] : "CLIENT X";
        BoardManager boardManager = new BoardManager();
        MoveFinder moveFinder = new MoveFinder(boardManager);

        NetworkClient nc = new NetworkClient("127.0.0.1", teamName, ImageIO.read(new File("/home/july/Projects/AI/logos/earth_bending_emblem_fill_by_mr_droy-d6xo95p.png")));

        nc.getTimeLimitInSeconds();

        nc.getExpectedNetworkLatencyInMilliseconds();

        boardManager.setTeamCode(nc.getMyPlayerNumber()); // 0 = rot, 1 = grün, 2 = blau
        moveFinder.setTeam();

        while (true) {
            Move receiveMove = nc.receiveMove();
            if (receiveMove == null) {
                Token token = moveFinder.getBestToken();
                Move move = new Move(token.x, token.y);
                System.out.println(teamName + " made Move: " + move.x + "," + move.y);
                nc.sendMove(move);
            } else {
                if(receiveMove.x < 0 || receiveMove.y < 0){
                    return;
                }
                System.out.println("Updating with Move: " + receiveMove.x + "," + receiveMove.y);
                boardManager.update(receiveMove);
            }
        }
    }
}