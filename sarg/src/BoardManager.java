import lenz.htw.sarg.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardManager {

    private int NUMBER_OF_PIECES_PER_PLAYER = 5;

    private int[][] directions = {
            {+1,  0}, { 0, -1}, {-1, -1},
            {-1,  0}, {0, +1}, { 1, 1},

    };

    private Team[] teams;
    private Token[][] board = new Token[9][9];
    private List<Token> own_tokens = new ArrayList<>();
    private Team own_team;

    public BoardManager(){
        teams = new Team[3];
        teams[TeamCode.RED.getCode()] = new Team(TeamCode.RED, new int[]{0,1}, new int[]{1,1});
        teams[TeamCode.GREEN.getCode()] = new Team(TeamCode.GREEN, new int[]{1,0}, new int[]{0,-1});
        teams[TeamCode.BLUE.getCode()] = new Team(TeamCode.BLUE, new int[]{-1,-1}, new int[]{-1,0});
        setUpTeams();
    }

    public void setTeamCode(int code){
        own_team = teams[code];
        setUpOwnTeam();
    }

    public void update(Move receiveMove) {
        Token token = board[receiveMove.x][receiveMove.y];
        int moved_team_code = token.getTeamCode();

        int[] movingDirection_Left = teams[moved_team_code].getmovingDirection_Left();
        int[] movingDirection_Right = teams[moved_team_code].getmovingDirection_Right();
        token.x = receiveMove.x + movingDirection_Right[0];
        token.y = receiveMove.y + movingDirection_Right[1];
        Token tokenLeft = new Token(receiveMove.x + movingDirection_Left[0], receiveMove.y + movingDirection_Left[1], moved_team_code);

        updateBoard(receiveMove, token, tokenLeft);

        if(moved_team_code == own_team.getTeamCode().getCode()){
            own_tokens.add(tokenLeft);
        }
    }

    private void updateBoard(Move receiveMove, Token tokenRight, Token tokenLeft) {
        board[tokenRight.x][tokenRight.y] = tokenRight;
        board[tokenLeft.x][tokenLeft.y] = tokenLeft;
        board[receiveMove.x][receiveMove.y] = null;
    }

    public Token chooseRandomPiece(){
        Random rand = new Random();
        return own_tokens.get(rand.nextInt(own_tokens.size()));
    }

    public int[][] getCurrentNeighbors(int[] position){
        int[][] neighbors = new int[6][2];
        for(int i = 0; i < directions.length; i++){
            neighbors[i] = new int[]{position[0] + directions[i][0], position[1] + directions[i][1]};
        }
        return neighbors;
    }

    private void setUpTeams(){
        for (int red = 0; red < NUMBER_OF_PIECES_PER_PLAYER; red++){
            Token token = new Token(red, 0, TeamCode.RED.getCode());
            board[token.x][token.y] = token;
        }
        for (int green = 0; green < NUMBER_OF_PIECES_PER_PLAYER; green++){
            Token token = new Token(green, green + 4, TeamCode.GREEN.getCode());
            board[token.x][token.y] = token;
        }
        for (int blue = 0; blue < NUMBER_OF_PIECES_PER_PLAYER; blue++){
            Token token = new Token(8, blue + 4, TeamCode.BLUE.getCode());
            board[token.x][token.y] = token;
        }
    }

    private void setUpOwnTeam(){
        for (int i = 0; i < board.length; i++){
            Token[] boardRow = board[i];
            for (int j = 0; j < boardRow.length; j++){
                if(boardRow[j] != null && boardRow[j].teamCode == own_team.getTeamCode().getCode()){
                    own_tokens.add(boardRow[j]);
                }
            }
        }
    }
}
