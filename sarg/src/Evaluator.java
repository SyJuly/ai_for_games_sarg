public class Evaluator {

    int teamCode;
    BoardManager boardManager;

    public Evaluator(int teamCode, BoardManager boardManager){
        this.teamCode = teamCode;
        this.boardManager = boardManager;
    }

    public AlphaBetaResult evaluate(BoardConfiguration boardConfig) {
        float evaluatedValue = 0;

        float a = 0.1f;
        float b = 0.59f;
        float c = 0.3f;
        float d = 0.01f;

        int activeTokens = 0;
        int tokensOutsideBoard = 0;
        int tokenDistanceToBorder = 0;
        int jumpProbability = 0;

        for (Token token: boardConfig.getCurrentTokensOfTeam(teamCode)) {
            activeTokens++;



            if(!boardManager.isValid(token.x, token.y)){
                tokensOutsideBoard++;
            }
        }

        evaluatedValue = a * activeTokens + b * tokensOutsideBoard + c * tokenDistanceToBorder + d * jumpProbability;
        return new AlphaBetaResult(Math.round(evaluatedValue));
    }
}
