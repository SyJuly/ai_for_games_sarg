package AI;

import Board.Token;

import java.util.Comparator;

public class TokenComparator implements Comparator<Token> {

    @Override
    public int compare(Token t1, Token t2) {
        if(t1.prevTurnABValue > t2.prevTurnABValue){
            return 1;
        } else if (t1.prevTurnABValue < t2.prevTurnABValue){
            return -1;
        }
        return 0;
    }
}
