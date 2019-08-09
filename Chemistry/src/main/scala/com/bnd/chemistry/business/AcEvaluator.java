package com.bnd.chemistry.business;

import java.util.*;
import java.util.Map.Entry;

import com.bnd.chemistry.domain.*;
import com.bnd.function.evaluator.FunctionEvaluator;
import com.bnd.function.evaluator.FunctionEvaluatorFactory;

public class AcEvaluator {

	private final AcEvaluation acEvaluation;
	private final AcTranslatedRun translatedRun;
	private final Map<AcTranslationVariable, List<Double>> translationItemHistoryIteratorMap;
	private final int translationSteps;

	private int actualStep = 0;
	private List<Double> evaluatedValues = new ArrayList<Double>();
	private FunctionEvaluator<Double, Double> functionEvaluator;

	protected AcEvaluator(
		AcEvaluation taskEvaluation,
		AcTranslatedRun translatedRun,
		FunctionEvaluatorFactory functionEvaluatorFactory
	) {
		this.acEvaluation = taskEvaluation;
		this.translatedRun = translatedRun;
		this.translationItemHistoryIteratorMap = initTranslationItemHistorySequenceIteratorMap(translatedRun);
		this.translationSteps = translatedRun.getSteps();
		this.functionEvaluator = functionEvaluatorFactory.createInstance(acEvaluation.getEvalFunction());
	}

	private Map<AcTranslationVariable, List<Double>> initTranslationItemHistorySequenceIteratorMap(AcTranslatedRun translatedRun) {
		Map<AcTranslationVariable, List<Double>> itemHistorySequenceIteratorMap = new HashMap<AcTranslationVariable, List<Double>>();
		for (AcTranslationItemHistory itemHistory : translatedRun.getItemHistories()) {
			itemHistorySequenceIteratorMap.put(itemHistory.getVariable(), itemHistory.getSequence());
		}
		return itemHistorySequenceIteratorMap;
	}

	public void evaluate() {
		if (actualStep >= translationSteps) {
			// we are done
			return;
		}
		final Map<Integer, Double> environment = createFunctionEnvironment();
		final Double evalValue = functionEvaluator.evaluate(environment);
		if (evalValue.isInfinite()) {
			System.out.println("Evaluation to an infinite number using enviroment " + environment.toString() + ".");
		}
		if (evalValue.isNaN()) {
			System.out.println("Evaluation to NaN using enviroment " + environment.toString() + ".");
		}
		evaluatedValues.add(functionEvaluator.evaluate(environment));
		actualStep += acEvaluation.getPeriodicTranslationsNumber();
	}

	private Map<Integer, Double> createFunctionEnvironment() {
		Map<Integer, Double> environment = new HashMap<Integer, Double>();
		for (int subStep = 0; subStep < acEvaluation.getPeriodicTranslationsNumber(); subStep++) {
			addTranslationItemsToEnvironment(environment, actualStep + subStep);
		}
		return environment;
	}

	private void addTranslationItemsToEnvironment(Map<Integer, Double> environment, int translationStep) {
		for (final Entry<AcTranslationVariable, List<Double>> translationItemLabelSequencePair : translationItemHistoryIteratorMap.entrySet()) {
			final List<Double> values = translationItemLabelSequencePair.getValue();
			if (translationStep < values.size()) {
				final Double translationItemValue = translationItemLabelSequencePair.getValue().get(translationStep);
				if (translationItemValue != null) {
					environment.put(translationItemLabelSequencePair.getKey().getVariableIndex(), translationItemValue);
				}
			}
		}
	}

	public void evaluateMultiple(int evaluationSteps) {
		for (int step = 0; step < evaluationSteps; step++) evaluate();
	}

	public void evaluateFull() {
		while (actualStep < translationSteps) evaluate();
	}

	public AcEvaluatedRun getEvaluatedRun() {
		AcEvaluatedRun evaluatedRun = new AcEvaluatedRun();
		evaluatedRun.setCreateTime(new Date());
		evaluatedRun.setEvaluation(acEvaluation);
		evaluatedRun.setTranslatedRun(translatedRun);

		evaluatedRun.setEvaluatedValues(evaluatedValues.toArray(new Double[0]));
		return evaluatedRun;
	}
}