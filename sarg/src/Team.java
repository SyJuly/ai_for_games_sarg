import java.util.ArrayList;
import java.util.List;

public class Team {
    private TeamCode code;
    private int[] movingDirection_Left;
    private int[] movingDirection_Right;
    public List<Token> belongingTokens;

    public Team(TeamCode code, int[] movingDirection_Left, int[] movingDirection_Right){
        this.code = code;
        this.movingDirection_Left = movingDirection_Left;
        this.movingDirection_Right = movingDirection_Right;
        belongingTokens = new ArrayList<>();
    }

    public TeamCode getTeamCode(){
        return code;
    }

    public int[] getmovingDirection_Left(){
        return movingDirection_Left;
    }

    public int[] getmovingDirection_Right(){
        return movingDirection_Right;
    }
}
