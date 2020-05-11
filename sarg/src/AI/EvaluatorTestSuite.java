package AI;

import Team.TeamRed;
import Team.TeamGreen;
import Team.TeamBlue;
import Team.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTestSuite {
    @Test
    public void testGetDistanceFromPointToLine(){
        TeamRed team = new TeamRed();
        assertEquals(Team.getDistanceFromPointToLine(4,0, team.lineBorder), 0);
        assertEquals(Team.getDistanceFromPointToLine(8,4, team.lineBorder), 0);
        assertTrue(Team.getDistanceFromPointToLine(4,1, team.lineBorder) < 1);
    }

    @Test
    public void testCalculateDistanceToClosestWinningBorder_teamRed(){
        TeamRed team = new TeamRed();
        assertEquals(0, team.calculateDistanceToClosestWinningBorder(4,0));
        assertEquals(1, team.calculateDistanceToClosestWinningBorder(7,6));
        assertEquals(1, team.calculateDistanceToClosestWinningBorder(6,7));
        assertEquals(0, team.calculateDistanceToClosestWinningBorder(8,8));
    }

    @Test
    public void testCalculateDistanceToClosestWinningBorder_teamGreen(){
        TeamGreen team = new TeamGreen();
        assertEquals(0, team.calculateDistanceToClosestWinningBorder(0,4));
        assertEquals(1, team.calculateDistanceToClosestWinningBorder(1,2));
        assertEquals(1, team.calculateDistanceToClosestWinningBorder(5,7));
        assertEquals(0, team.calculateDistanceToClosestWinningBorder(8,4));
    }

    @Test
    public void testCalculateDistanceToClosestWinningBorder_teamBlue(){
        TeamBlue team = new TeamBlue();
        assertEquals(0, team.calculateDistanceToClosestWinningBorder(4,0));
        assertEquals(1, team.calculateDistanceToClosestWinningBorder(1,1));
        assertEquals(1, team.calculateDistanceToClosestWinningBorder(2,1));
        assertEquals(0, team.calculateDistanceToClosestWinningBorder(0,4));
    }
}