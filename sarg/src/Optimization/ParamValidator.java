package Optimization;

import AI.EvaluationParameter;
import Logging.Logger;

public class ParamValidator {
    private static final double MAX_PARAM_VALUE = 0.9999999999998;
    private static final double MIN_PARAM_VALUE = 0.0000000000001;
    private Logger logger;

    public ParamValidator(Logger logger){
        this.logger = logger;
    }

    public EvaluationParameter getValidatedMutatedParams(double activeTokensPercentage, double successfulTokensPercentage, double tokenDistanceToBorderPercentage, double[] mutations) {
        double[] mutatedParams = new double[]{activeTokensPercentage + mutations[0], successfulTokensPercentage + mutations[1], tokenDistanceToBorderPercentage + mutations[2]};
        double[] validatedMutatedParams = new double[mutatedParams.length];
        boolean correctionNeeded = false;
        int correctionTries = 0;
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
        validatedMutatedParams = mutatedParams;

        double correction = (1.0 - getParamsSum(validatedMutatedParams))/validatedMutatedParams.length;
        while(correction == 0){
            correctionTries++;
            correctionNeeded = true;

            for(int i = 0; i < validatedMutatedParams.length - 1; i++){
                validatedMutatedParams[i] = validatedMutatedParams[i] + correction;
            }
            validatedMutatedParams[validatedMutatedParams.length - 1] =  validatedMutatedParams[validatedMutatedParams.length - 1] + (1.0 - getParamsSum(validatedMutatedParams));
            correction = (1.0 - getParamsSum(validatedMutatedParams))/validatedMutatedParams.length;
            if(correctionTries > 20){
                continue;
            }
        }


        if(correctionNeeded){
            logger.logMutationCorrection(mutatedParams, validatedMutatedParams, correctionTries);
        }

        return new EvaluationParameter(
                validatedMutatedParams[0],
                validatedMutatedParams[1],
                validatedMutatedParams[2]);
    }

    private double getParamsSum(double[] mutatedParams) {
        double sum = 0;
        for(int i = 0; i < mutatedParams.length; i++){
            sum += mutatedParams[i];
        }
        return sum;
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
