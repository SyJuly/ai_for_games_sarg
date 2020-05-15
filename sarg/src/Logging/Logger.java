package Logging;

import AI.EvaluationParameter;
import Optimization.Player;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {

    private PrintWriter out;
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_HH-mm");

    public Logger() throws FileNotFoundException {
        out = new PrintWriter("/home/july/Projects/AI/sarg/logs/log" + sdf.format(new Date()) + ".txt");
    }

    public void logGameOver(int[] score, int winner, Player[] players, int numberOfTurnsToVictory) {
        boolean winnerError = false;
        winner = winner - 1;
        String log = "          Game over. \n";
        for(int i = 0; i < score.length; i++){
            log+= "         Player " + players[i].name + "/ Team " + players[i].getTeamCode() + ": " + score[players[i].getTeamCode()];
            if(players[i].getTeamCode() == winner){
                log+= " <--WINNER ON SERVER within " + numberOfTurnsToVictory  + " turns";
                if(score[players[i].getTeamCode()] < 5){
                    winnerError = true;
                }
            }
            log+="\n";
        }
        //log+= "WINNER ON SERVER:" + winner + " within " + numberOfTurnsToVictory  + " turns";
        writeToLogFile(log);

        if(winnerError){
            String error = "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! \n ERROR OCCURED. \n Winner on server didn't match the calculated winner. \n !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";
            writeToLogFile(error);
        }
    }

    private void writeToLogFile(String log){
        out.write("\n \n");
        out.write(log);
        System.out.println(log);
    }

    public void logEvaluation(Player[] players, EvaluationParameter[] evaluationParameters) {
        String log = "                  Evaluation. \n";
        for (int i = 0; i < evaluationParameters.length; i++) {
            log+= "                 Player " + players[i].name + "| " + evaluationParameters[i] + "\n";
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

    public void logMediumEvaluation(double[][] evaluatedValues, EvaluationParameter[] evaluationParameters) {
        String log = "Calculating medium parameter evaluation from "+ evaluatedValues[0].length +" games played. \n";
        for(int i = 0; i < evaluationParameters.length; i++){
            log += Arrays.toString(evaluatedValues[i]) + " ----> Writing medium:" + evaluationParameters[i] + "\n";
        }
        writeToLogFile(log);
    }

    public void logMutation(double[] high_mutations, double[] low_mutations) {
        String log = "Creating mutations. \n";
        log += "High Mutations: " + Arrays.toString(high_mutations) + "\n";
        log += "Low_Mutations: " + Arrays.toString(low_mutations) + "\n";
        writeToLogFile(log);
    }

}
