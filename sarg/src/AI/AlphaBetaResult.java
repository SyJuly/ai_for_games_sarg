package AI;

import Board.Token;

public class AlphaBetaResult {
    public Token token;
    public int value;

    public AlphaBetaResult(int value){
        this.value = value;
    }

    public static AlphaBetaResult getMaxAlphaBetaResult(AlphaBetaResult first, AlphaBetaResult second){
        if(first.value >= second.value){
            return first;
        } else {
            return second;
        }
    }

    public static AlphaBetaResult getMinAlphaBetaResult(AlphaBetaResult first, AlphaBetaResult second){
        if(first.value < second.value){
            return first;
        } else {
            return second;
        }
    }
}
