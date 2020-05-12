package Team;

import Board.Token;

import java.util.ArrayList;
import java.util.List;

public abstract class Team {
    protected int[] movingDirection_Left;
    protected int[] movingDirection_Right;
    public int[][] lineBorder;
    public List<Token> belongingTokens;
    public int totalNumOfSuccessfulTokens = 0;

    public Team(){
        belongingTokens = new ArrayList<>();
    }


    public int[] getmovingDirection_Left(){
        return movingDirection_Left;
    }

    public int[] getmovingDirection_Right(){
        return movingDirection_Right;
    }

    public abstract double calculateDistanceToClosestWinningBorder(int x, int y);
    public abstract TeamCode getTeamCode();

    public static double getDistanceFromPointToLine(int x, int y, int[][] line)
    {
        return Math.abs((line[1][0] - line[0][0])*(line[0][1] - y) - (line[0][0] - x)*(line[1][1] - line[0][1]))/
                Math.sqrt(Math.pow(line[1][0] - line[0][0], 2) + Math.pow(line[1][1] - line[0][1], 2));
    }
}
