package Optimization;

public class GameResult {
    public int[] scoreOfTeams;
    public int numOfTurnsToGameOver;
    public int winner;
    public Player[] players;

    public GameResult(Player[] players, int[] scoreOfTeams, int numOfTurnsToGameOver, int winner){
        this.scoreOfTeams = scoreOfTeams;
        this.numOfTurnsToGameOver = numOfTurnsToGameOver;
        this.winner = winner;
        this.players = players;

    }
}
