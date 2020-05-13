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
    private EvaluationParameter params;

    public Evaluator(int teamCode, BoardManager boardManager, EvaluationParameter params){
        this.teamCode = teamCode;
        this.boardManager = boardManager;
        this.params = params;
    }

    public AlphaBetaResult evaluate(BoardConfiguration boardConfig) {
        if(boardConfig.getSuccessfulTokensOfTeam(teamCode) >= NUM_OF_SUCCESSFUL_TOKENS_TO_WIN){
            return new AlphaBetaResult(Integer.MAX_VALUE);

        }
        List<Token> activeTeamTokensOnBoard = boardConfig.getCurrentTokensOfTeam(teamCode);

        double evaluatedValue;

        double a = 0.2;
        double b = 0.6;
        double c = 0.2;

        int activeTokens = activeTeamTokensOnBoard.size();
        int successfulTokens = boardConfig.getSuccessfulTokensOfTeam(teamCode);
        int tokenDistanceToBorder = 0;

        for (Token token: activeTeamTokensOnBoard) {
            tokenDistanceToBorder-=boardConfig.teams[token.teamCode].calculateDistanceToClosestWinningBorder(token.x, token.y);
        }

        evaluatedValue = params.activeTokensPercentage * activeTokens + params.successfulTokensPercentage * successfulTokens + params.tokenDistanceToBorderPercentage * tokenDistanceToBorder;

        if(counter < 10){
            counter++;
            //System.out.println("ActiveTokens: " + a + "*" + activeTokens + " | TokensOutSideBoard: " + b + "*" + tokensOutsideBoard + " | TokenDistanceToBorder: " + c + "*" + tokenDistanceToBorder + " =====> " + evaluatedValue + " = rounded: " + Math.round(evaluatedValue));
        }
        return new AlphaBetaResult(evaluatedValue);
    }
}
