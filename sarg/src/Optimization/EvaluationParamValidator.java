package Optimization;

import AI.EvaluationParameter;
import Logging.Logger;

public class EvaluationParamValidator {
    private static final double MAX_PARAM_VALUE = 0.9999999999999;
    private static final double MIN_PARAM_VALUE = 0.0000000000001;
    private Logger logger;

    public EvaluationParamValidator(Logger logger){
        this.logger = logger;
    }

    public EvaluationParameter getValidatedMutatedParams(double activeTokensPercentage, double successfulTokensPercentage, double tokenDistanceToBorderPercentage, double[] mutations) {
        double[] mutatedParams = new double[]{activeTokensPercentage + mutations[0], successfulTokensPercentage + mutations[1], tokenDistanceToBorderPercentage + mutations[2]};
        double[] validatedMutatedParams = new double[mutatedParams.length];
        boolean correctionNeeded = false;
        for(int i = 0; i < mutatedParams.length; i++){
            double validatedParam = mutatedParams[i];
            double correction;
            if(validatedParam > 1){
                correction = validatedParam - MAX_PARAM_VALUE;
                validatedParam = MAX_PARAM_VALUE;
                balanceCorrection(mutatedParams, i, correction);
                correctionNeeded = true;
            } else if (validatedParam < 0){
                correction = validatedParam - MIN_PARAM_VALUE;
                validatedParam = MIN_PARAM_VALUE;
                balanceCorrection(mutatedParams, i, correction);
                correctionNeeded = true;
            }
            validatedMutatedParams[i] = validatedParam;
        }

        if(correctionNeeded){
            logger.logMutationCorrection(mutatedParams, validatedMutatedParams);
        }

        return new EvaluationParameter(
                mutatedParams[0],
                mutatedParams[1],
                mutatedParams[2]);
    }

    private void balanceCorrection(double[] paramValues, int i, double correction) {
        int nextParamIndex = getNextParam(i, paramValues);
        int secondNextParamIndex = getNextParam(nextParamIndex, paramValues);
        paramValues[nextParamIndex] += correction / 2.0;
        paramValues[secondNextParamIndex] += correction / 2.0;
    }

    private int getNextParam(int index, double[] param){
        index++;
        if(index > param.length - 1){
            return 0;
        }
        return index;
    }
}
