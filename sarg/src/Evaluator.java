public class Evaluator {

    int teamCode;
    BoardManager boardManager;
    int counter = 0;

    public Evaluator(int teamCode, BoardManager boardManager){
        this.teamCode = teamCode;
        this.boardManager = boardManager;
    }

    public AlphaBetaResult evaluate(BoardConfiguration boardConfig) {
        int evaluatedValue;

        int a = 2;
        int b = 6;
        int c = 2;

        int activeTokens = 0;
        int tokensOutsideBoard = 0;
        int tokenDistanceToBorder = 0;

        for (Token token: boardConfig.getCurrentTokensOfTeam(teamCode)) {
            activeTokens++;

            if(!boardManager.isValid(token.x, token.y)){
                tokensOutsideBoard++;
            } else {
                tokenDistanceToBorder-=boardConfig.teams[token.teamCode].calculateDistanceToClosestWinningBorder(token.x, token.y);
            }
        }

        evaluatedValue = a * activeTokens + b * tokensOutsideBoard + c * tokenDistanceToBorder;

        if(counter < 10){
            counter++;
            System.out.println("ActiveTokens: " + a + "*" + activeTokens + " | TokensOutSideBoard: " + b + "*" + tokensOutsideBoard + " | TokenDistanceToBorder: " + c + "*" + tokenDistanceToBorder + " =====> " + evaluatedValue + " = rounded: " + Math.round(evaluatedValue));
        }
        return new AlphaBetaResult(evaluatedValue);
    }
}
