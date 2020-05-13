package AI;

public class EvaluationParameter {
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

    public String toString() {
        return "Q = " + activeTokensPercentage + " * activeTokens + " + successfulTokensPercentage + " * successfulTokens + " + tokenDistanceToBorderPercentage + " * tokenDistanceToBorder";
    }
}
