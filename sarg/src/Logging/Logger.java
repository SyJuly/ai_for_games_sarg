package Logging;

import AI.EvaluationParameter;
import Board.BoardConfiguration;
import Board.Token;
import Optimization.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

public class Logger {

    private PrintWriter out;
    private String logDir = "/home/july/Projects/AI/sarg/logs/";
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_HH-mm");
    private String currDir;
    private String fileType = ".txt";

    public Logger() throws FileNotFoundException {
        //out = new PrintWriter("/home/july/Projects/AI/sarg/logs/log" + sdf.format(new Date()) + ".txt");
        currDir = logDir + "log" + sdf.format(new Date()) + "/";
        new File(currDir).mkdir();
        out = new PrintWriter(currDir + "init" + fileType);

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
        out.close();
        try {
            out = new PrintWriter(currDir + index + fileType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String log = "----------------------------------------------------------------------\n Starting Generation (Number: " + index + ").";
        writeToLogFile(log);
    }

    public void stop() {
        out.close();
        System.out.println("STOPPING LOGGER.");
    }

    public void logMediumEvaluation(Double[][] evaluatedValues, EvaluationParameter[] evaluationParameters) {
        String log = "Calculating medium parameter evaluation from "+ evaluatedValues[0].length +" games played. \n";
        for(int i = 0; i < evaluationParameters.length; i++){
            log += Arrays.toString(evaluatedValues[i]) + " ----> Writing medium:" + evaluationParameters[i] + "\n";
        }
        writeToLogFile(log);
    }

    public void logMutation(double[][] high_mutations, double[][] low_mutations) {
        String log = "Creating mutations. \n";
        log += "High Mutations: ";
        for(int i = 0; i < high_mutations.length; i++){
            log+= Arrays.toString(high_mutations[i]);
        }
        log += "\nLow_Mutations: ";
        for(int i = 0; i < low_mutations.length; i++){
            log+= Arrays.toString(low_mutations[i]);
        }
        writeToLogFile(log);
    }

    public void logMutationCorrection(double[] mutatedParams, double[] validatedMutatedParams) {
        String log = "CORRECTED mutations to be in range 0 to 1. \n";
        log += "Invalid Parameters: " + Arrays.toString(mutatedParams) + "\n";
        log += "Valid Parameters: " + Arrays.toString(validatedMutatedParams) + "\n";
        writeToLogFile(log);
    }

    public void logDraw() {
        String log = "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! \n DRAW DETECTED. Skipping game. \n";
        writeToLogFile(log);
    }

    public void logTeamTokens(BoardConfiguration boardConfig) {
        String log = "Team tokens. \n";
        for(int i = 0; i < boardConfig.teams.length; i++){
            log+= "Team "+ boardConfig.teams[i].getTeamCode() + ": "+ Arrays.toString(boardConfig.teams[i].belongingTokens.toArray()) + "\n";
        }
        writeToLogFile(log);
    }

    public void logMove(Token token) {
        String log = "Move"+ token +". \n";
        writeToLogFile(log);
    }

    public void logBeforeMove() {
        String log = "_________________________________________________________________________________________\n";
        writeToLogFile(log);
    }

    public void logDepthReport(LogDepthReport[] depthReports) {
        String log = "--------------------------------------------------\n--------------------------------------------------\nDepth report.\n";
        for(int j = 0; j < depthReports.length; j++) {
            int[] depthLogging = depthReports[j].depthLogging;
            int[] depths = depthReports[j].depths;
            int teamCode = depthReports[j].teamCode;
            log+= "     Team " + teamCode + ":\n";
            for (int i = 0; i < depthLogging.length - 1; i++) {
                log += "        Depth " + depths[i] + " was reached " + depthLogging[i] + " times.\n";
            }
            log += "        Fallback choosing random token happened " + depthLogging[depthLogging.length - 1] + " times.\n";

        }
        log += "--------------------------------------------------\n--------------------------------------------------\n";
        writeToLogFile(log);
    }

    public void logErrror(Exception e) {
        String log = "!!!!!!!!!!!!!!!!!!!!!!!!!!!ERROR DETECTED!!!!!!!!!!!!!!!!!!!!!!!!!. \n\n\n";
        log += e.getMessage() + "\n\n\n";
        writeToLogFile(log);
    }

    public void logTeamLostPrematurely(int teamCode) {
        String log = "INFO: Team " + teamCode + " lost prematurely (probably because it had no more tokens to play). \n\n";
        writeToLogFile(log);
    }
}
