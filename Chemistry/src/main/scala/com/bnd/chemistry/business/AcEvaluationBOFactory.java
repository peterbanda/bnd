package com.bnd.chemistry.business;

import java.io.Serializable;

import com.bnd.chemistry.domain.AcEvaluation;
import com.bnd.chemistry.domain.AcTranslatedRun;
import com.bnd.function.evaluator.FunctionEvaluatorFactory;

public class AcEvaluationBOFactory implements Serializable {

	private final FunctionEvaluatorFactory functionEvaluatorFactory;

	public AcEvaluationBOFactory(FunctionEvaluatorFactory functionEvaluatorFactory) {
		this.functionEvaluatorFactory = functionEvaluatorFactory;
	}

	public AcEvaluator createInstance(AcEvaluation taskEvaluation, AcTranslatedRun translatedRun) {
		return new AcEvaluator(taskEvaluation, translatedRun, functionEvaluatorFactory);
	}
}