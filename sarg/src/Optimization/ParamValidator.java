package Optimization;

import AI.EvaluationParameter;
import Logging.Logger;

public class ParamValidator {
    private Logger logger;

    public ParamValidator(Logger logger){
        this.logger = logger;
    }

    public EvaluationParameter getValidatedMutatedParams(double activeTokensPercentage, double successfulTokensPercentage, double tokenDistanceToBorderPercentage, double[] mutations) {
        double[] mutatedParams = new double[]{activeTokensPercentage + mutations[0], successfulTokensPercentage + mutations[1], tokenDistanceToBorderPercentage + mutations[2]};
        double[] validatedMutatedParams = new double[mutatedParams.length];
        double sum = 0;
        for(int i = 0; i < mutatedParams.length; i++){
            sum += mutatedParams[i];
        }
        for(int i = 0; i < mutatedParams.length; i++){
            validatedMutatedParams[i] = mutatedParams[i] / sum;
        }

        return new EvaluationParameter(
                validatedMutatedParams[0],
                validatedMutatedParams[1],
                validatedMutatedParams[2]);
    }
}
