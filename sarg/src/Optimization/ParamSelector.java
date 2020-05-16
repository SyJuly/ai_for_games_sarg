package Optimization;

import AI.EvaluationParameter;

public class ParamSelector {

    private ParamEvaluationExecutor paramEvaluationExecutor;

    public ParamSelector(ParamEvaluationExecutor paramEvaluationExecutor) {
        this.paramEvaluationExecutor = paramEvaluationExecutor;
    }

    public EvaluationParameter[] selectParentParams(EvaluationParameter[][] currParamGen) {
        EvaluationParameter[] matchChampions = getMediumMatchChampionsOfCurrentParamGen(currParamGen);
        paramEvaluationExecutor.runMatchWithEvaluation(matchChampions);
        return matchChampions; // selection
    }

    private EvaluationParameter[] getMediumMatchChampionsOfCurrentParamGen(EvaluationParameter[][] currParamGen) {
        EvaluationParameter[] champions = new EvaluationParameter[currParamGen.length];
        for (int c = 0; c < currParamGen.length; c++) {
            champions[c] = currParamGen[c][0];
            for (int r = 1; r < currParamGen[c].length; r++) {
                if (currParamGen[c][r].evaluationValue > champions[c].evaluationValue) {
                    champions[c] = currParamGen[c][r];
                }
            }
        }
        return champions;
    }
}