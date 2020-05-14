package Logging;

import AI.EvaluationParameter;
import Optimization.Player;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private PrintWriter out;
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_HH-mm");

    public Logger() throws FileNotFoundException {
        out = new PrintWriter("/home/july/Projects/AI/sarg/logs/log" + sdf.format(new Date()) + ".txt");
    }

    public void logGameOver(int[] score, int winnerIndex, Player[] players, String[] names, int numberOfTurnsToVictory) {
        String log = "Game over. \n";
        for(int i = 0; i < score.length; i++){
            log+= "Player " + players[i].name + "/ Team " + players[i].getTeamCode() + ": " + score[players[i].getTeamCode()];
            if(i == winnerIndex){
                log+= " <--WINNER ON SERVER within " + numberOfTurnsToVictory  + " turns";
            }
            log+="\n";
        }
        writeToLogFile(log);
    }

    private void writeToLogFile(String log){
        out.write("\n \n");
        out.write(log);
        System.out.println(log);
    }

    public void logEvaluation(Player[] players, EvaluationParameter[] evaluationParameters) {
        String log = "Evaluation. \n";
        for (int i = 0; i < evaluationParameters.length; i++) {
            log+= "Player " + players[i].name + "| " + evaluationParameters[i] + "\n";
        }
        writeToLogFile(log);
    }

    public void logRecombination(EvaluationParameter[][] paramGen) {
        String log = "Recombination. \n";
        for (int i = 0; i < paramGen.length; i++) {
            for (int j = 0; j < paramGen.length; j++) {
                if(j == 0){
                    log+= "Parent: ";
                } else {
                    log+= "Child: ";
                }
                log+= paramGen[i][j] + "\n";
            }
        }
        writeToLogFile(log);
    }

    public void logNewGeneration(int index) {
        String log = "----------------------------------------------------------------------\n Starting Generation (Number: " + index + ").";
        writeToLogFile(log);
    }

    public void stop() {
        out.close();
    }
}
