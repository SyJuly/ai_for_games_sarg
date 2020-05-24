package AI;

import Board.BoardConfiguration;
import Board.BoardManager;
import Board.Token;

import java.util.List;

public class Evaluator {

    public static final int NUM_OF_SUCCESSFUL_TOKENS_TO_WIN = 5;
    private int teamCode;
    private EvaluationParameter params;

    public Evaluator(int ownTeamCode, EvaluationParameter params){
        this.teamCode = ownTeamCode;
        this.params = params;
    }

    public AlphaBetaResult evaluate(BoardConfiguration boardConfig, int depth) {
        if(isVictorious(boardConfig, teamCode)){
            return new AlphaBetaResult(Double.POSITIVE_INFINITY, depth);

        }

        List<Token> activeTeamTokensOnBoard = boardConfig.getCurrentTokensOfTeam(teamCode);

        int opponentA = boardConfig.getNextTeam(teamCode);
        int opponentB = boardConfig.getNextTeam(opponentA);

        if(isVictorious(boardConfig, opponentA)
        || isVictorious(boardConfig, opponentB)
        || activeTeamTokensOnBoard.isEmpty()){
            return new AlphaBetaResult(Double.NEGATIVE_INFINITY, depth);

        }


        double evaluatedValue;

        int activeTokens = activeTeamTokensOnBoard.size();
        int successfulTokens = boardConfig.getSuccessfulTokensOfTeam(teamCode);
        int tokenDistanceToBorder = 0;

        for (Token token: activeTeamTokensOnBoard) {
            tokenDistanceToBorder-=boardConfig.teams[token.teamCode].calculateDistanceToClosestWinningBorder(token.x, token.y);
        }

        evaluatedValue = params.activeTokensPercentage * activeTokens + params.successfulTokensPercentage * successfulTokens + params.tokenDistanceToBorderPercentage * tokenDistanceToBorder;

        return new AlphaBetaResult(evaluatedValue, depth);
    }

    private boolean isVictorious(BoardConfiguration boardConfig, int teamCodeToCheck) {
        return boardConfig.getSuccessfulTokensOfTeam(teamCodeToCheck) >= NUM_OF_SUCCESSFUL_TOKENS_TO_WIN;
    }
}
