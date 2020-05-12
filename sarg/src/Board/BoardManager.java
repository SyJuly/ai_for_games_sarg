package Board;

import lenz.htw.sarg.Move;

public class BoardManager {

    private int NUMBER_OF_PIECES_PER_PLAYER = 5;
    private int BOARD_SIZE = 9;

    private BoardBoundary boardBoundary;
    private BoardConfiguration boardConfig;
    private boolean isGameOver = false;

    public BoardManager(){
        boardBoundary = new BoardBoundary(new double[][]{
                {-0.1,-0.1}, {-0.1,4.1}, {4.1,8.1}, {8.1,8.1}, {8.1,4.1}, {4.1,-0.1}
        });


        Token[][] board = new Token[BOARD_SIZE][BOARD_SIZE];
        boardConfig = new BoardConfiguration(board, NUMBER_OF_PIECES_PER_PLAYER);
    }

    public void update(Move receiveMove) {
        boardConfig = chooseToken(boardConfig, receiveMove.x, receiveMove.y);
        isGameOver = boardConfig.isGameOver();
        boardConfig.printScore();
    }

    public BoardConfiguration chooseToken(BoardConfiguration currBoardConfig, int x, int y){
        BoardConfiguration nextBoardConfig = currBoardConfig.copyConfig();

        Token[][] board = nextBoardConfig.board;
        Token token = board[x][y];
        int moved_team_code = token.getTeamCode();

        int[] movingDirection_Left = nextBoardConfig.teams[moved_team_code].getmovingDirection_Left();
        int[] movingDirection_Right = nextBoardConfig.teams[moved_team_code].getmovingDirection_Right();

        Token tokenLeft = new Token(x , y , moved_team_code);

        token = moveToken(nextBoardConfig, token, movingDirection_Right, false);
        tokenLeft = moveToken(nextBoardConfig, tokenLeft, movingDirection_Left, true);

        updateBoard(nextBoardConfig, x,y, token, tokenLeft);
        return nextBoardConfig;
    }

    private Token moveToken(BoardConfiguration currBoardConfig, Token token, int[] movingDirection, boolean addToTeamList) {
        boolean startRemovingJumpedByTokens = false;
        while(currBoardConfig.board[token.x][token.y] != null){
            if(startRemovingJumpedByTokens){
                cleanUpToken(currBoardConfig,token.x, token.y);
            }
            token.x = token.x + movingDirection[0];
            token.y = token.y + movingDirection[1];
            if(!isValid(token.x, token.y)){
                if(!addToTeamList){
                    currBoardConfig.removeTokenFromTeamList(token);

                }
                currBoardConfig.addSuccessfulTokenToTeam(token);
                return null;
            }
            startRemovingJumpedByTokens = true;
        }
        currBoardConfig.addTokenToTeamList(token);
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

    public boolean isValid(int x, int y){
        return boardBoundary.contains(x,y);
    }

    public BoardConfiguration getCurrentBoardConfig() {
        return boardConfig;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
