package Optimization;

public class GameResult {
    public int[] scoreOfTeams;
    public boolean[] teamsLostPrematurely;
    public int numOfTurnsToGameOver;
    public int winner;
    public Player[] players;

    public GameResult(Player[] players, int[] scoreOfTeams, int numOfTurnsToGameOver, int winner, boolean[] teamsLostPrematurely){
        this.scoreOfTeams = scoreOfTeams;
        this.numOfTurnsToGameOver = numOfTurnsToGameOver;
        this.winner = winner;
        this.players = players;
        this.teamsLostPrematurely = teamsLostPrematurely;
    }
}
