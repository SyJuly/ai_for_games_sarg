package Optimization;

import lenz.htw.sarg.Server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EvolutionaryOptimization {
    public static void main(String[] args) throws IOException {

        BufferedImage image = ImageIO.read(new File("/home/july/Projects/AI/logos/earth_bending_emblem_fill_by_mr_droy-d6xo95p.png"));

        Server server = new Server();

        Player[] players = new Player[3];
        Thread[] playerThreads = new Thread[3];
        String[] names = new String[]{"A", "B", "C"};

        long timeStartedRunning = System.currentTimeMillis();
        while(System.currentTimeMillis() - timeStartedRunning < 10000){
            // wait
        }

        for(int i = 0; i < players.length; i++){
            boolean createDumpPlayer = i < 1;
            players[i] = new Player(3, createDumpPlayer, image, names[i]);
            playerThreads[i] = new Thread(players[i]);
            playerThreads[i].start();
        }

        int winner = server.runOnceAndReturnTheWinner(3);
        System.out.println("WINNER IS " + winner);
        int winner2 = server.runOnceAndReturnTheWinner(3);
        System.out.println("WINNER IS " + winner2);

        for(int i = 0; i < players.length; i++){
            players[i].stop();
        }
    }
}
