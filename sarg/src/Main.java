import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lenz.htw.sarg.Move;
import lenz.htw.sarg.net.NetworkClient;

public class Main {

    public static void main(String[] args) throws IOException {

        // initialisieren... z.B. Spielbrett
        BoardManager board = new BoardManager();

        NetworkClient nc = new NetworkClient("127.0.0.1", "CLIENT 0", ImageIO.read(new File("/home/july/Projects/AI/logos/earth_bending_emblem_fill_by_mr_droy-d6xo95p.png")));

        nc.getTimeLimitInSeconds();

        nc.getExpectedNetworkLatencyInMilliseconds();

        board.setTeamCode(nc.getMyPlayerNumber()); // 0 = rot, 1 = grün, 2 = blau

        while (true) {
            Move receiveMove = nc.receiveMove();
            if (receiveMove == null) {
                int[] piece = board.chooseRandomPiece();
                Move move = new Move(piece[0], piece[1]);
                nc.sendMove(move);
            } else {
                // integriereZugInSpielbrett(move);
            }
        }
    }
}