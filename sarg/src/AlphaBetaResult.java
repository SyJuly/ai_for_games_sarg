public class AlphaBetaResult {
    public Token token;
    public int value;

    public AlphaBetaResult(Token token, int value){
        this.token = token;
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
