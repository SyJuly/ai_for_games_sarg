package AI;

import Board.BoardConfiguration;
import Board.BoardManager;
import Board.Token;

import java.util.List;

public class Evaluator {

    public static final int NUM_OF_SUCCESSFUL_TOKENS_TO_WIN = 5;
    private int teamCode;
    private BoardManager boardManager;
    private int counter = 0;

    public Evaluator(int teamCode, BoardManager boardManager){
        this.teamCode = teamCode;
        this.boardManager = boardManager;
    }

    public AlphaBetaResult evaluate(BoardConfiguration boardConfig) {
        if(boardConfig.getSuccessfulTokensOfTeam(teamCode) >= NUM_OF_SUCCESSFUL_TOKENS_TO_WIN){
            return new AlphaBetaResult(Integer.MAX_VALUE);

        }
        List<Token> activeTeamTokensOnBoard = boardConfig.getCurrentTokensOfTeam(teamCode);

        int evaluatedValue;

        int a = 2;
        int b = 6;
        int c = 2;

        int activeTokens = activeTeamTokensOnBoard.size();
        int successfulTokens = boardConfig.getSuccessfulTokensOfTeam(teamCode);
        int tokenDistanceToBorder = 0;

        for (Token token: activeTeamTokensOnBoard) {
            tokenDistanceToBorder-=boardConfig.teams[token.teamCode].calculateDistanceToClosestWinningBorder(token.x, token.y);
        }

        evaluatedValue = a * activeTokens + b * successfulTokens + c * tokenDistanceToBorder;

        if(counter < 10){
            counter++;
            //System.out.println("ActiveTokens: " + a + "*" + activeTokens + " | TokensOutSideBoard: " + b + "*" + tokensOutsideBoard + " | TokenDistanceToBorder: " + c + "*" + tokenDistanceToBorder + " =====> " + evaluatedValue + " = rounded: " + Math.round(evaluatedValue));
        }
        return new AlphaBetaResult(evaluatedValue);
    }
}
