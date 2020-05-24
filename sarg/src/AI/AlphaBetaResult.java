package AI;

import Board.Token;

public class AlphaBetaResult {
    public Token token;
    public double value;
    public int depth;

    public AlphaBetaResult(double value, int depth){
        this.value = value;
        this.depth = depth;
    }

    public static AlphaBetaResult getMaxAlphaBetaResult(AlphaBetaResult first, AlphaBetaResult second){
        if(first.value > second.value){
            return first;
        } else if(first.value == second.value){
            if(first.token == null || second.token == null) {
                return getValidResult(first, second);
            } else if(first.depth > second.depth){
                if(first.value == Double.POSITIVE_INFINITY){
                    System.out.println("Choosing: " + first.token + " with " + first.depth + " because win would be in " + second.depth + " otherwise.");
                }
                return first;
            } else {
                if(first.value == Double.POSITIVE_INFINITY){
                    System.out.println("Choosing: " + second.token + " with " + second.depth + " because win would be in " + first.depth + " otherwise.");
                }
                return second;
            }
        } else{
            return second;
        }
    }

    private static AlphaBetaResult getValidResult(AlphaBetaResult first, AlphaBetaResult second) {
        return first.token == null ? second : first;
    }

    private static AlphaBetaResult getMostRelevantResult(AlphaBetaResult first, AlphaBetaResult second) {
        if(first.token == null || second.token == null) {
            return getValidResult(first, second);
        } else if(first.depth > second.depth){
            return first;
        } else {
            return second;
        }
    }

    public static AlphaBetaResult getMinAlphaBetaResult(AlphaBetaResult first, AlphaBetaResult second){
        if(first.value < second.value){
            return first;
        } else if(first.value == second.value){
            return getMostRelevantResult(first, second);
        } else{
            return second;
        }
    }
}
