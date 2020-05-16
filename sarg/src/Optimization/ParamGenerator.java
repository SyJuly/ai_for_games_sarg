package Optimization;

import AI.EvaluationParameter;
import Logging.Logger;

import java.util.Arrays;
import java.util.Random;

public class ParamGenerator {

    private final int HIGH_MUTATION_MAX = 1000;
    private final int LOW_MUTATION_MAX = 5000;
    private final int NUM_INDIVIUUMS_PER_GEN = 9;
    private int NUM_OF_PLAYERS;
    private Logger logger;
    private Random random;
    private ParamValidator validator;

    public ParamGenerator(Logger logger, int NUM_OF_PLAYERS){
        this.logger = logger;
        this.NUM_OF_PLAYERS = NUM_OF_PLAYERS;
        validator = new ParamValidator(logger);
        random = new Random(1);
    }

    public EvaluationParameter[][] recombineAndMutateSelectedParamsToNewGen(EvaluationParameter[] parentParams){
        Arrays.sort(parentParams);
        int totalEvaluatedValuesForGen = 0;
        double[][] high_Mutations = new double[][]{getMutation(HIGH_MUTATION_MAX), getMutation(HIGH_MUTATION_MAX), getMutation(HIGH_MUTATION_MAX)};
        double[][] low_Mutations = new double[][]{new double[]{0,0,0}, getMutation(LOW_MUTATION_MAX), getMutation(LOW_MUTATION_MAX)};
        logger.logMutation(high_Mutations, low_Mutations);

        EvaluationParameter[][] newParamGen = new EvaluationParameter[NUM_INDIVIUUMS_PER_GEN/NUM_OF_PLAYERS][NUM_OF_PLAYERS]; // 9 values per generation
        for(int i = 0; i < parentParams.length; i++){
            totalEvaluatedValuesForGen += parentParams[i].evaluationValue;
        }


        double activeTokensPercentage = 0;
        double successfulTokensPercentage = 0;
        double tokenDistanceToBorderPercentage = 0;
        for(int p = 0; p < parentParams.length; p++){
            double base = parentParams[p].evaluationValue/totalEvaluatedValuesForGen;
            activeTokensPercentage += parentParams[p].activeTokensPercentage * base;
            successfulTokensPercentage += parentParams[p].successfulTokensPercentage * base;
            tokenDistanceToBorderPercentage += parentParams[p].tokenDistanceToBorderPercentage * base;
        }

        for(int i = 0; i < newParamGen.length; i++){
            newParamGen[i][0] = parentParams[i]; // first has no mutation
            for(int j = 1; j < newParamGen.length; j++){
                double[][] mutations = j == newParamGen.length -1 ? high_Mutations : low_Mutations;
                newParamGen[i][j] = validator.getValidatedMutatedParams(activeTokensPercentage, successfulTokensPercentage, tokenDistanceToBorderPercentage, mutations[i]);
            }
        }
        logger.logRecombination(newParamGen);
        return newParamGen;
    }


    private double[] getMutation(int max){
        int activeTokensModifier = random.nextInt(max*2) - max;
        int successfulTokensModifier = random.nextInt(max*2) - max;
        int tokenDistanceToBorderModifier = - activeTokensModifier - successfulTokensModifier;
        return new double[]{activeTokensModifier/(10.0 * max), successfulTokensModifier/(10.0 * max), tokenDistanceToBorderModifier/(10.0 * max)};
    }
}
