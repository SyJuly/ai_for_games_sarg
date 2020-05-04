import java.util.ArrayList;
import java.util.List;

public class Team {
    protected TeamCode code;
    protected int[] movingDirection_Left;
    protected int[] movingDirection_Right;
    public List<Token> belongingTokens;

    public Team(){
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
