package Optimization;

import AI.EvaluationParameter;

public class EvaluationParamValidator {
    private static final double MAX_PARAM_VALUE = 0.9999999999999;
    private static final double MIN_PARAM_VALUE = 0.0000000000001;

    public static EvaluationParameter getValidatedMutatedParams(double activeTokensPercentage, double successfulTokensPercentage, double tokenDistanceToBorderPercentage, double[] mutations) {
        double[] mutatedParamValues = new double[]{activeTokensPercentage + mutations[0], successfulTokensPercentage + mutations[1], tokenDistanceToBorderPercentage + mutations[2]};

        for(int i = 0; i < mutatedParamValues.length; i++){
            double validatedParam = mutatedParamValues[i];
            double correction;
            if(validatedParam > 1){
                correction = validatedParam - MAX_PARAM_VALUE;
                validatedParam = MAX_PARAM_VALUE;
                balanceCorrection(mutatedParamValues, i, correction);
            } else if (validatedParam < 0){
                correction = validatedParam - MIN_PARAM_VALUE;
                validatedParam = MIN_PARAM_VALUE;
                balanceCorrection(mutatedParamValues, i, correction);
            }
            mutatedParamValues[i] = validatedParam;
        }

        return new EvaluationParameter(
                mutatedParamValues[0],
                mutatedParamValues[1],
                mutatedParamValues[2]);
    }

    private static void balanceCorrection(double[] paramValues, int i, double correction) {
        int nextParamIndex = getNextParam(i, paramValues);
        int secondNextParamIndex = getNextParam(nextParamIndex, paramValues);
        paramValues[nextParamIndex] += correction / 2.0;
        paramValues[secondNextParamIndex] += correction / 2.0;
    }

    private static int getNextParam(int index, double[] param){
        index++;
        if(index > param.length - 1){
            return 0;
        }
        return index;
    }
}
