import lenz.htw.sarg.Move;
import org.lwjgl.Sys;

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
    private BoardKonfiguration boardConfig;
    private Team[] teams;
    private Team own_team;

    public BoardManager(){
        boardBoundary = new BoardBoundary(new int[][]{
                {0,0}, {0,4}, {4,8}, {8,8}, {8,4}, {4,0}
        });
        teams = new Team[3];
        teams[TeamCode.RED.getCode()] = new Team(TeamCode.RED, new int[]{0,1}, new int[]{1,1});
        teams[TeamCode.GREEN.getCode()] = new Team(TeamCode.GREEN, new int[]{1,0}, new int[]{0,-1});
        teams[TeamCode.BLUE.getCode()] = new Team(TeamCode.BLUE, new int[]{-1,-1}, new int[]{-1,0});

        Token[][] board = new Token[BOARD_SIZE][BOARD_SIZE];
        boardConfig = new BoardKonfiguration(teams, board, NUMBER_OF_PIECES_PER_PLAYER);
    }

    public void setTeamCode(int code){
        own_team = teams[code];
    }

    public void update(Move receiveMove) {
        boardConfig = chooseToken(boardConfig, receiveMove.x, receiveMove.y);
    }

    private BoardKonfiguration chooseToken(BoardKonfiguration currBoardConfig, int x, int y){
        BoardKonfiguration nextBoardConfig = currBoardConfig.copyConfig();

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

    private Token moveToken(BoardKonfiguration currBoardConfig, Token token, int[] movingDirection) {
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

    private void cleanUpToken(BoardKonfiguration currBoardConfig, int x, int y) {
        Token token = currBoardConfig.board[x][y];
        currBoardConfig.removeTokenFromTeamList(token);
        currBoardConfig.board[token.x][token.y] = null;
    }

    private void updateBoard(BoardKonfiguration currBoardConfig, int x, int y, Token tokenRight, Token tokenLeft) {
        if(tokenRight != null){
            currBoardConfig.board[tokenRight.x][tokenRight.y] = tokenRight;
        }
        if(tokenLeft != null){
            currBoardConfig.board[tokenLeft.x][tokenLeft.y] = tokenLeft;
        }
        currBoardConfig.board[x][y] = null;
    }

    public Token chooseRandomPiece(){
        Random rand = new Random();
        return own_team.belongingTokens.get(rand.nextInt(own_team.belongingTokens.size()));
    }

    public Token getBestToken(){
        System.out.println("Choosing next token");
        int depth = 1;
        int ownTeamCode = own_team.getTeamCode().getCode();
        AlphaBetaResult result = alphaBeta(ownTeamCode, boardConfig, depth, Integer.MIN_VALUE, Integer.MAX_VALUE,null);
        return result.token;
    }

    private AlphaBetaResult alphaBeta(int teamCode,
                                      BoardKonfiguration boardConfig,
                                      int depth,
                                      int alpha,
                                      int beta,
                                      Token choosenToken) {
        if(depth == 0 || boardConfig.areMoreTurnsPossible(own_team.getTeamCode().getCode()) /*should be when player has 5 points*/) {
            return evaluate(boardConfig);
        }
        if(teamCode == own_team.getTeamCode().getCode()){
            AlphaBetaResult maxEval = new AlphaBetaResult(Integer.MIN_VALUE);
            List<Token> tokensToChooseFrom = boardConfig.getCurrentTokensOfTeam(teamCode);
            for(int i = 0; i < tokensToChooseFrom.size(); i++){
                Token token = tokensToChooseFrom.get(i);
                BoardKonfiguration newBoardConfig = chooseToken(boardConfig,token.x, token.y);
                AlphaBetaResult evaluation = alphaBeta(getNextTeam(teamCode), newBoardConfig, depth - 1, alpha, beta, token);
                evaluation.token = token;
                maxEval = AlphaBetaResult.getMaxAlphaBetaResult(maxEval, evaluation);
                alpha = Math.max(alpha, evaluation.value);
                if(beta <= alpha){
                    break;
                }
            }
            return maxEval;
        } else {
            AlphaBetaResult minEval =  new AlphaBetaResult(Integer.MAX_VALUE);
            List<Token> tokensToChooseFrom = boardConfig.getCurrentTokensOfTeam(teamCode);
            for(int i = 0; i < tokensToChooseFrom.size(); i++){
                Token token = tokensToChooseFrom.get(i);
                BoardKonfiguration newBoardConfig = chooseToken(boardConfig,token.x, token.y);
                AlphaBetaResult evaluation = alphaBeta(getNextTeam(teamCode), newBoardConfig, depth - 1, alpha, beta, token);
                evaluation.token = token;
                minEval = AlphaBetaResult.getMinAlphaBetaResult(minEval, evaluation);
                beta = Math.min(beta, evaluation.value);
                if(beta <= alpha){
                    break;
                }
            }
            return minEval;
        }
    }

    private AlphaBetaResult evaluate(BoardKonfiguration boardConfig) {
        int evaluatedValue = 0;
        for (Token token: boardConfig.getCurrentTokensOfTeam(own_team.getTeamCode().getCode())) {
            if(!isValid(token.x, token.y)){
                evaluatedValue++;
            }
        }
        return new AlphaBetaResult(evaluatedValue);
    }

    private int getNextTeam(int lastTeamCode){
        lastTeamCode++;
        if(lastTeamCode > teams.length -1){
            return 0;
        }
        return lastTeamCode;
    }

    public int[][] getCurrentNeighbors(int[] position){
        int[][] neighbors = new int[6][2];
        for(int i = 0; i < directions.length; i++){
            neighbors[i] = new int[]{position[0] + directions[i][0], position[1] + directions[i][1]};
        }
        return neighbors;
    }

    private boolean isValid(int x, int y){
        return boardBoundary.contains(x,y);
    }
}
