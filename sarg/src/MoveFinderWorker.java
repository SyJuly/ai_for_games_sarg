import org.lwjgl.Sys;

import java.util.List;

public class MoveFinderWorker implements Runnable {
    private final int DEFAULT_DEPTH = 10;

    public int id;
    private Team ownTeam;
    private BoardManager boardManager;
    private MoveFinder moveFinder;

    public AlphaBetaResult bestResult;

    public MoveFinderWorker(int id, MoveFinder moveFinder, BoardManager boardManager){
        this.moveFinder = moveFinder;
        this.id = id;
        this.boardManager = boardManager;
    }


    @Override
    public void run() {
        System.out.println("Starting to find best token in worker.");
        int ownTeamCode = ownTeam.getTeamCode().getCode();
        bestResult = alphaBeta(ownTeamCode, boardManager.getCurrentBoardConfig(), DEFAULT_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);
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

    public void setOwnTeam(Team ownTeam) {
        this.ownTeam = ownTeam;
    }
}
