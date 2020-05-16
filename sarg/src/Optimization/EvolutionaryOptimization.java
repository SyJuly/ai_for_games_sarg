package Optimization;

import AI.EvaluationParameter;
import Logging.Logger;

import java.io.IOException;

public class EvolutionaryOptimization {

    private final int NUM_OF_PLAYERS = 3;
    private final int GAMEPLAY_ITERATION_PER_GEN = 2;
    private final int NUMBER_OF_GENERATIONS = 2;

    private Logger logger;
    private ParamGenerator paramGenerator;
    private ParamEvaluationExecutor paramEvaluationExecutor;
    private ParamSelector paramSelector;

    private EvaluationParameter[] initialParamParents = new EvaluationParameter[]{
        new EvaluationParameter(0.2,0.6,0.2),
        new EvaluationParameter(0.15,0.8,0.5),
        new EvaluationParameter(0.3,0.4,0.3)
    };


    public void runEvolutionaryOptimization() throws IOException {

        logger = new Logger();
        paramGenerator = new ParamGenerator(logger, NUM_OF_PLAYERS);
        paramEvaluationExecutor = new ParamEvaluationExecutor(logger, NUM_OF_PLAYERS, GAMEPLAY_ITERATION_PER_GEN);
        paramSelector = new ParamSelector(paramEvaluationExecutor);

        EvaluationParameter[][] currParamGen;
        EvaluationParameter[] selectedParams = initialParamParents;
        paramEvaluationExecutor.runMatchWithEvaluation(selectedParams);

        for(int i = 0; i < NUMBER_OF_GENERATIONS; i++) {
            logger.logNewGeneration(i);
            currParamGen = paramGenerator.recombineAndMutateSelectedParamsToNewGen(selectedParams);
            for (int g = 0; g < currParamGen.length; g++) {
                paramEvaluationExecutor.runMatchWithEvaluation(currParamGen[g]);
            }
            selectedParams = paramSelector.selectParentParams(currParamGen);
        }

        logger.stop();
        System.exit(0);
    }



    public static void main(String[] args) throws IOException {
        new EvolutionaryOptimization().runEvolutionaryOptimization();
    }
}
