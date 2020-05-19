package Board;

public class Token {
    public int x;
    public int y;
    public int teamCode;
    public double prevTurnABValue;

    public Token(int x, int y, int teamCode){
        this.x = x;
        this.y = y;
        this.teamCode = teamCode;
    }

    public Token(int x, int y, int teamCode, double prevTurnABValue){
        this.x = x;
        this.y = y;
        this.teamCode = teamCode;
        this.prevTurnABValue = prevTurnABValue;
    }

    public int getTeamCode(){
        return teamCode;
    }

    public String toString() {
        return "(" + teamCode + "|" + x + "," + y + "):" + prevTurnABValue;
    }
}
