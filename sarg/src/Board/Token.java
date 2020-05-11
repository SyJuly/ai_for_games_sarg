package Board;

public class Token {
    public int x;
    public int y;
    public int teamCode;

    public Token(int x, int y, int teamCode){
        this.x = x;
        this.y = y;
        this.teamCode = teamCode;
    }

    public int getTeamCode(){
        return teamCode;
    }

    public String toString() {
        return "(" + teamCode + "|" + x + "," + y + ")";
    }
}
