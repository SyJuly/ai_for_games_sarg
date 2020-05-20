package AI;

import Board.BoardConfiguration;
import Board.BoardManager;
import Board.Token;
import Team.Team;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class MoveFinderWorker implements Callable {
    private int depth;
    public int id;
    private int ownTeamCode;
    private BoardManager boardManager;
    private Evaluator evaluator;
    private AlphaBetaResult[] results;
    private boolean isCancelled = false;

    public MoveFinderWorker(int id, AlphaBetaResult[] results, BoardManager boardManager, int depth){
        this.id = id;
        this.results = results;
        this.boardManager = boardManager;
        this.depth = depth;
    }

    public void cancelTask() {
        isCancelled = true;
    }

    @Override
    public AlphaBetaResult call() {
        isCancelled = false;
        results[id] = alphaBeta(ownTeamCode, boardManager.getCurrentBoardConfig(), depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        /*if(ownTeamCode == 0){
            boardManager.getCurrentBoardConfig().printScore();
        }*/
        return results[id];
    }

    private AlphaBetaResult alphaBeta(int teamCode,
                                      BoardConfiguration boardConfig,
                                      int currDepth,
                                      double alpha,
                                      double beta) {

        if(isCancelled){
            return null;
        }
        if(currDepth == 0 || boardConfig.areMoreTurnsPossible(teamCode)) {
            return evaluator.evaluate(boardConfig);
        }
        if(teamCode == ownTeamCode){
            AlphaBetaResult maxEval = new AlphaBetaResult(Double.NEGATIVE_INFINITY);
            List<Token> tokensToChooseFrom = boardConfig.getCurrentTokensOfTeam(teamCode);
            for(int i = 0; i < tokensToChooseFrom.size(); i++){
                Token token = tokensToChooseFrom.get(i);
                AlphaBetaResult evaluation = getAlphaBetaResultFromNextTraverse(teamCode, boardConfig, currDepth, alpha, beta, token);
                maxEval = AlphaBetaResult.getMaxAlphaBetaResult(maxEval,evaluation);
                alpha = Math.max(evaluation.value, alpha);
                if(beta <= alpha){
                    break;
                }
            }
            return maxEval;
        } else {
            AlphaBetaResult minEval =  new AlphaBetaResult(Double.POSITIVE_INFINITY);
            List<Token> tokensToChooseFrom = boardConfig.getCurrentTokensOfTeam(teamCode);
            for(int i = 0; i < tokensToChooseFrom.size(); i++){
                Token token = tokensToChooseFrom.get(i);
                AlphaBetaResult evaluation = getAlphaBetaResultFromNextTraverse(teamCode, boardConfig, currDepth, alpha, beta, token);
                minEval = AlphaBetaResult.getMinAlphaBetaResult(minEval, evaluation);
                beta = Math.min(evaluation.value, beta);
                if(beta <= alpha){
                    break;
                }
            }
            return minEval;
        }
    }

    private AlphaBetaResult getAlphaBetaResultFromNextTraverse(int teamCode, BoardConfiguration boardConfig, int currDepth, double alpha, double beta, Token token) {
        BoardConfiguration newBoardConfig = boardManager.chooseToken(boardConfig, token.x, token.y);
        AlphaBetaResult evaluation = alphaBeta(newBoardConfig.getNextTeam(teamCode), newBoardConfig, currDepth - 1, alpha, beta);
        evaluation.token = token;
        if(currDepth == depth){
            token.prevTurnABValue = evaluation.value;
        }
        return evaluation;
    }



    public void setupMoveFinderWorker(int ownTeamCode, EvaluationParameter params) {
        this.ownTeamCode = ownTeamCode;
        params.teamCode = ownTeamCode;
        this.evaluator = new Evaluator(ownTeamCode, boardManager, params);
    }
}
