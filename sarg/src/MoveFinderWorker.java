import java.util.List;
import java.util.concurrent.Callable;

public class MoveFinderWorker implements Callable {
    private int depth;
    public int id;
    private Team ownTeam;
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
        int ownTeamCode = ownTeam.getTeamCode().getCode();
        results[id] = alphaBeta(ownTeamCode, boardManager.getCurrentBoardConfig(), depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return results[id];
    }

    private AlphaBetaResult alphaBeta(int teamCode,
                                      BoardConfiguration boardConfig,
                                      int depth,
                                      int alpha,
                                      int beta) {

        if(isCancelled){
            return null;
        }
        if(depth == 0 || boardConfig.areMoreTurnsPossible(ownTeam.getTeamCode().getCode())) {
            return evaluator.evaluate(boardConfig);
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



    public void setOwnTeam(Team ownTeam) {
        this.ownTeam = ownTeam;
        this.evaluator = new Evaluator(ownTeam.getTeamCode().getCode(), boardManager);
    }
}
