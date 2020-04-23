import lenz.htw.sarg.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BoardManager {

    private int NUMBER_OF_PIECES_PER_PLAYER = 5;
    private int BOARD_SIZE = 9;

    private int[][] directions = {
            {+1,  0}, { 0, -1}, {-1, -1},
            {-1,  0}, {0, +1}, { 1, 1},

    };

    private BoardBoundary boardBoundary;
    private Team[] teams;
    private Token[][] board = new Token[BOARD_SIZE][BOARD_SIZE];
    private List<Token> own_tokens = new ArrayList<>();
    private Team own_team;

    public BoardManager(){
        boardBoundary = new BoardBoundary(new int[][]{
                {0,0}, {0,4}, {4,8}, {8,8}, {8,4}, {4,0}
        });
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
        chooseToken(board, receiveMove.x, receiveMove.y);
    }

    private void chooseToken(Token[][] currBoard, int x, int y){
        Token token = board[x][y];
        int moved_team_code = token.getTeamCode();

        int[] movingDirection_Left = teams[moved_team_code].getmovingDirection_Left();
        int[] movingDirection_Right = teams[moved_team_code].getmovingDirection_Right();

        Token tokenLeft = new Token(x , y , moved_team_code);

        token = moveToken(token, movingDirection_Right);
        tokenLeft = moveToken(tokenLeft, movingDirection_Left);

        if(moved_team_code == own_team.getTeamCode().getCode()){
            if(tokenLeft != null){
                own_tokens.add(tokenLeft);
            }
            //System.out.println(Arrays.toString(own_tokens.toArray()));
        }
        updateBoard(currBoard, x,y, token, tokenLeft);
    }

    private Token moveToken(Token token, int[] movingDirection) {
        boolean startRemovingJumpedByTokens = false;
        while(board[token.x][token.y] != null){
            if(startRemovingJumpedByTokens){
                cleanUpToken(board[token.x][token.y]);
            }
            token.x = token.x + movingDirection[0];
            token.y = token.y + movingDirection[1];
            if(!isValid(token.x, token.y)){
                if(own_tokens.contains(token)){
                    own_tokens.remove(token);
                }
                return null;
            }
            startRemovingJumpedByTokens = true;
        }
        return token;
    }

    private void cleanUpToken(Token token) {
        if(own_tokens.contains(token)){
            own_tokens.remove(token);
        }
        board[token.x][token.y] = null;
    }

    private void updateBoard(Token[][] currBoard, int x, int y, Token tokenRight, Token tokenLeft) {
        if(tokenRight != null){
            currBoard[tokenRight.x][tokenRight.y] = tokenRight;
        }
        if(tokenLeft != null){
            currBoard[tokenLeft.x][tokenLeft.y] = tokenLeft;
        }
        currBoard[x][y] = null;
    }

    public Token chooseRandomPiece(){
        Random rand = new Random();
        return own_tokens.get(rand.nextInt(own_tokens.size()));
    }

    public Token getBestToken(){
        int depth = 4;
        alphaBeta(own_tokens, depth, true);
    }

    private int alphaBeta(List<Token> tokensToChooseFrom, int depth, boolean maximizingPlayer) {
        if(depth == 0 && tokensToChooseFrom.isEmpty() /*should be when player has 5 points*/) {
            return evaluate(tokensToChooseFrom);
        }
        if(maximizingPlayer){
            int maxEval = Integer.MIN_VALUE;
            for(int i = 0; i < tokensToChooseFrom.size(); i++){
                List<Token> nextTokensToChooseFrom = chooseToken();
                int evaluation = alphaBeta(tokensToChooseFrom, depth - 1, false);
                maxEval = Math.max(maxEval, evaluation);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for(int i = 0; i < tokensToChooseFrom.size(); i++){
                int evaluation = alphaBeta(tokensToChooseFrom, depth - 1, false);
                minEval = Math.min(minEval, evaluation);
            }
            return minEval;
        }
    }

    private int evaluate(List<Token> tokensToChooseFrom) {
        int evaluatedValue = 0;
        for (Token token: tokensToChooseFrom) {
            if(!isValid(token.x, token.y)){
                evaluatedValue++;
            }
        }
        return evaluatedValue;
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

    private boolean isValid(int x, int y){
        return boardBoundary.contains(x,y);
    }
}
