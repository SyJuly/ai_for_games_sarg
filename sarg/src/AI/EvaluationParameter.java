package AI;

public class EvaluationParameter implements Comparable<EvaluationParameter>{
    public double activeTokensPercentage;
    public double successfulTokensPercentage;
    public double tokenDistanceToBorderPercentage;
    public double evaluationValue;
    public int teamCode;

    public EvaluationParameter(double activeTokensPercentage, double successfulTokensPercentage, double tokenDistanceToBorderPercentage){
        this.activeTokensPercentage = activeTokensPercentage;
        this.successfulTokensPercentage = successfulTokensPercentage;
        this.tokenDistanceToBorderPercentage = tokenDistanceToBorderPercentage;
    }

    public EvaluationParameter(double activeTokensPercentage, double successfulTokensPercentage, double tokenDistanceToBorderPercentage, double evaluationValue){
        this.activeTokensPercentage = activeTokensPercentage;
        this.successfulTokensPercentage = successfulTokensPercentage;
        this.tokenDistanceToBorderPercentage = tokenDistanceToBorderPercentage;
        this.evaluationValue = evaluationValue;
    }

    public String toString() {
        return "Q = " + activeTokensPercentage + " * activeTokens + " + successfulTokensPercentage + " * successfulTokens + " + tokenDistanceToBorderPercentage + " * tokenDistanceToBorder (EV: " + evaluationValue + ")";
    }

    @Override
    public int compareTo(EvaluationParameter o) {
        return evaluationValue > o.evaluationValue ? 1 : -1;
    }
}
