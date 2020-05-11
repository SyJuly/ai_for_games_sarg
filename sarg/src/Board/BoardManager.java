package Board;

import Team.Team;
import Team.TeamRed;
import Team.TeamGreen;
import Team.TeamBlue;
import Team.TeamCode;
import lenz.htw.sarg.Move;

public class BoardManager {

    private int NUMBER_OF_PIECES_PER_PLAYER = 5;
    private int BOARD_SIZE = 9;

    private BoardBoundary boardBoundary;
    private BoardConfiguration boardConfig;
    private Team[] teams;
    private Team own_team;

    public BoardManager(){
        boardBoundary = new BoardBoundary(new int[][]{
                {0,0}, {0,4}, {4,8}, {8,8}, {8,4}, {4,0}
        });
        teams = new Team[3];
        teams[TeamCode.RED.getCode()] = new TeamRed();
        teams[TeamCode.GREEN.getCode()] = new TeamGreen();
        teams[TeamCode.BLUE.getCode()] = new TeamBlue();

        Token[][] board = new Token[BOARD_SIZE][BOARD_SIZE];
        boardConfig = new BoardConfiguration(teams, board, NUMBER_OF_PIECES_PER_PLAYER);
    }

    public void setTeamCode(int code){
        own_team = teams[code];
    }

    public void update(Move receiveMove) {
        boardConfig = chooseToken(boardConfig, receiveMove.x, receiveMove.y);
    }

    public BoardConfiguration chooseToken(BoardConfiguration currBoardConfig, int x, int y){
        BoardConfiguration nextBoardConfig = currBoardConfig.copyConfig();

        Token[][] board = nextBoardConfig.board;
        Token token = board[x][y];
        int moved_team_code = token.getTeamCode();

        int[] movingDirection_Left = nextBoardConfig.teams[moved_team_code].getmovingDirection_Left();
        int[] movingDirection_Right = nextBoardConfig.teams[moved_team_code].getmovingDirection_Right();

        Token tokenLeft = new Token(x , y , moved_team_code);

        token = moveToken(nextBoardConfig, token, movingDirection_Right);
        tokenLeft = moveToken(nextBoardConfig, tokenLeft, movingDirection_Left);

        nextBoardConfig.addTokenToTeamList(tokenLeft);

        updateBoard(nextBoardConfig, x,y, token, tokenLeft);
        return nextBoardConfig;
    }

    private Token moveToken(BoardConfiguration currBoardConfig, Token token, int[] movingDirection) {
        boolean startRemovingJumpedByTokens = false;
        while(currBoardConfig.board[token.x][token.y] != null){
            if(startRemovingJumpedByTokens){
                cleanUpToken(currBoardConfig,token.x, token.y);
            }
            token.x = token.x + movingDirection[0];
            token.y = token.y + movingDirection[1];
            if(!isValid(token.x, token.y)){
                currBoardConfig.removeTokenFromTeamList(token);
                return null;
            }
            startRemovingJumpedByTokens = true;
        }

        return token;
    }

    private void cleanUpToken(BoardConfiguration currBoardConfig, int x, int y) {
        Token token = currBoardConfig.board[x][y];
        currBoardConfig.removeTokenFromTeamList(token);
        currBoardConfig.board[token.x][token.y] = null;
    }

    private void updateBoard(BoardConfiguration currBoardConfig, int x, int y, Token tokenRight, Token tokenLeft) {
        if(tokenRight != null){
            currBoardConfig.board[tokenRight.x][tokenRight.y] = tokenRight;
        }
        if(tokenLeft != null){
            currBoardConfig.board[tokenLeft.x][tokenLeft.y] = tokenLeft;
        }
        currBoardConfig.board[x][y] = null;
    }


    public int getNextTeam(int lastTeamCode){
        lastTeamCode++;
        if(lastTeamCode > teams.length -1){
            return 0;
        }
        return lastTeamCode;
    }

    public boolean isValid(int x, int y){
        return boardBoundary.contains(x,y);
    }

    public Team getOwnTeam() {
        return own_team;
    }

    public BoardConfiguration getCurrentBoardConfig() {
        return boardConfig;
    }
}
