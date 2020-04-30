import java.util.List;
import java.util.Random;

public class MoveFinder {
    private final int DEFAULT_DEPTH = 5;

    private int[][] directions = {
            {+1,  0}, { 0, -1}, {-1, -1},
            {-1,  0}, {0, +1}, { 1, 1},

    };

    private Team ownTeam;
    private BoardManager boardManager;

    public MoveFinder(BoardManager boardManager){
        this.ownTeam = ownTeam;
        this.boardManager = boardManager;
    }

    public Token chooseRandomPiece(){
        Random rand = new Random();
        return ownTeam.belongingTokens.get(rand.nextInt(ownTeam.belongingTokens.size()));
    }

    public Token getBestToken(){
        System.out.println("Choosing next token");
        int ownTeamCode = ownTeam.getTeamCode().getCode();
        AlphaBetaResult result = alphaBeta(ownTeamCode, boardManager.getCurrentBoardConfig(), DEFAULT_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return result.token;
    }

    private AlphaBetaResult alphaBeta(int teamCode,
                                      BoardConfiguration boardConfig,
                                      int depth,
                                      int alpha,
                                      int beta) {
        if(depth == 0 || boardConfig.areMoreTurnsPossible(ownTeam.getTeamCode().getCode())) {
            return evaluate(boardConfig);
        }
        if(teamCode == ownTeam.getTeamCode().getCode()){
            AlphaBetaResult maxEval = new AlphaBetaResult(Integer.MIN_VALUE);
            List<Token> tokensToChooseFrom = boardConfig.getCurrentTokensOfTeam(teamCode);
            for(int i = 0; i < tokensToChooseFrom.size(); i++){
                Token token = tokensToChooseFrom.get(i);
                AlphaBetaResult evaluation = getAlphaBetaResultFromNextTraverse(teamCode, boardConfig, depth, alpha, beta, token);
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
                AlphaBetaResult evaluation = getAlphaBetaResultFromNextTraverse(teamCode, boardConfig, depth, alpha, beta, token);
                minEval = AlphaBetaResult.getMinAlphaBetaResult(minEval, evaluation);
                beta = Math.min(beta, evaluation.value);
                if(beta <= alpha){
                    break;
                }
            }
            return minEval;
        }
    }

    private AlphaBetaResult getAlphaBetaResultFromNextTraverse(int teamCode, BoardConfiguration boardConfig, int depth, int alpha, int beta, Token token) {
        BoardConfiguration newBoardConfig = boardManager.chooseToken(boardConfig, token.x, token.y);
        AlphaBetaResult evaluation = alphaBeta(boardManager.getNextTeam(teamCode), newBoardConfig, depth - 1, alpha, beta);
        evaluation.token = token;
        return evaluation;
    }

    private AlphaBetaResult evaluate(BoardConfiguration boardConfig) {
        int evaluatedValue = 0;
        for (Token token: boardConfig.getCurrentTokensOfTeam(ownTeam.getTeamCode().getCode())) {
            if(!boardManager.isValid(token.x, token.y)){
                evaluatedValue++;
            }
        }
        return new AlphaBetaResult(evaluatedValue);
    }


    public int[][] getCurrentNeighbors(int[] position){
        int[][] neighbors = new int[6][2];
        for(int i = 0; i < directions.length; i++){
            neighbors[i] = new int[]{position[0] + directions[i][0], position[1] + directions[i][1]};
        }
        return neighbors;
    }

    public void setTeam(){
        ownTeam = boardManager.getOwnTeam();
    }
}
