package com.bnd.chemistry.business.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.domain.*;
import com.bnd.core.Pair;
import com.bnd.core.util.ConversionUtil;
import com.bnd.core.util.ObjectUtil;
import com.bnd.core.util.RandomUtil;
import com.bnd.function.BndFunctionException;
import com.bnd.function.business.FunctionFactory;
import com.bnd.function.business.FunctionUtility;
import com.bnd.function.business.jep.NormalRandomFunction;
import com.bnd.function.business.jep.RandomFunction;
import com.bnd.function.domain.BooleanFunction;
import com.bnd.function.domain.BooleanFunctionType;
import com.bnd.function.domain.Expression;
import com.bnd.function.domain.Function;
import com.bnd.function.evaluator.FunctionEvaluator;
import com.bnd.function.evaluator.FunctionEvaluatorFactory;
import com.bnd.math.domain.rand.RandomDistribution;
import com.bnd.math.domain.rand.ShapeLocationDistribution;
import com.bnd.math.domain.rand.UniformDistribution;

public class AcInteractionSeriesFactory {

	private final FunctionFactory functionFactory = new FunctionFactory();
	private final FunctionUtility functionUtility = new FunctionUtility();
	private final FunctionEvaluatorFactory functionEvaluatorFactory;

	public AcInteractionSeriesFactory(FunctionEvaluatorFactory functionEvaluatorFactory) {
		this.functionEvaluatorFactory = functionEvaluatorFactory;
	}

	public AcInteractionSeries createTemporalActionASWithInit(
		AcSpeciesSet speciesSet,
		BooleanFunctionType functionType,
		int timeWindowLength,
		RandomDistribution<Double> initInternalConcDistribution,
		RandomDistribution<Double> initWeightConcDistribution,
		AcConcentrationLevel inputConcentrationLevel,
		AcConcentrationLevel feedbackConcentrationLevel,
		int actionsNum,
		int afterInitStartingTimeStep,
		int actionInterval,
		boolean nullOutputSpecies
	) {
		AcInteractionSeries actionSeries = new AcInteractionSeries();
		actionSeries.setSpeciesSet(speciesSet);
		AcInteraction initAction = new AcInteraction();
		initAction.setStartTime(0);
		for (AcSpecies species : speciesSet.getInternalSpecies()) {
			AcSpeciesInteraction speciesAction = new AcSpeciesInteraction();
			speciesAction.setSpecies(species);
			speciesAction.setSettingFunction(createRandomFunction(initInternalConcDistribution));
			initAction.addToSpeciesActions(speciesAction);
		}
		for (AcSpecies species : speciesSet.getSpecies(AcSpeciesType.Functional)) {
			AcSpeciesInteraction speciesAction = new AcSpeciesInteraction();
			speciesAction.setSpecies(species);
			speciesAction.setSettingFunction(createRandomFunction(initWeightConcDistribution));
			initAction.addToSpeciesActions(speciesAction);
		}
		actionSeries.addAction(initAction);
		final Collection<AcInteraction> temporalFunctionActions = createTemporalFunctionActions(
			speciesSet,
			functionType,
			timeWindowLength,
			inputConcentrationLevel,
			feedbackConcentrationLevel,
			actionsNum,
			afterInitStartingTimeStep,
			actionInterval,
			nullOutputSpecies);
		actionSeries.addActions(temporalFunctionActions);
		return actionSeries;
	}

	public Function<Double, Double> createRandomFunction(final RandomDistribution<Double> randomDistribution) {
		switch (randomDistribution.getType()) {
		case Uniform:
			final UniformDistribution<Double> uniformRD = (UniformDistribution<Double>) randomDistribution;
			return createUniformRandomFunction(uniformRD.getFrom(), uniformRD.getTo());
		case Normal:
			final ShapeLocationDistribution<Double> normalRD = (ShapeLocationDistribution<Double>) randomDistribution;
			return createNormalRandomFunction(normalRD.getLocation(), normalRD.getShape());
		default:
			throw new BndFunctionException("Random distribution type '" + randomDistribution.getType() + "' not recognized.");
		}
	}

	private Function<Double, Double> createUniformRandomFunction(double from, double to) {
		return createFunctionFormula(RandomFunction.TAG, new Double[]{from, to});
	}

	private Function<Double, Double> createNormalRandomFunction(double mean, double variance) {
		return createFunctionFormula(NormalRandomFunction.TAG, new Double[]{mean, variance});
	}

	private Function<Double, Double> createFunctionFormula(String functionTag, Double[] inputs) {
		final String formula = functionUtility.getFunctionFormula(functionTag, ConversionUtil.toStrings(inputs));
		return new Expression<Double, Double>(formula);
	}

	private Collection<AcInteraction> createTemporalFunctionActions(
		AcSpeciesSet speciesSet,
		BooleanFunctionType functionType,
		int timeWindowLength,
		AcConcentrationLevel inputConcentrationLevel,
		AcConcentrationLevel feedbackConcentrationLevel,
		int actionsNum,
		int startingTime,
		int actionInterval,
		boolean nullOutputSpecies
	) {
		Collection<AcInteraction> actions = new ArrayList<AcInteraction>();
		int actualTime = startingTime; 
		final Collection<Pair<Boolean, Boolean>> inputDesiredOutputSeries = createInputOutputTemporalFunSeries(
				functionType, actionsNum, timeWindowLength);
		for (Pair<Boolean, Boolean> inputWithDesiredOutput : inputDesiredOutputSeries) {
			AcInteraction action = createAction(
				speciesSet,
				inputWithDesiredOutput.getFirst(),
				inputWithDesiredOutput.getSecond(),
				inputConcentrationLevel,
				feedbackConcentrationLevel,
				nullOutputSpecies);
			action.setStartTime(actualTime);
			actions.add(action);
			actualTime += actionInterval;
		}
		return actions;
	}

	private AcInteraction createAction(
		AcSpeciesSet speciesSet,
		Boolean inputValue,
		Boolean feedbackValue,
		AcConcentrationLevel inputConcentrationLevel,
		AcConcentrationLevel feedbackConcentrationLevel,
		boolean nullOutputSpecies
	) {
		AcInteraction action = new AcInteraction();
		final Collection<AcSpeciesInteraction> inputSpeciesActions = createBinarySpeciesActions(speciesSet.getSpecies(AcSpeciesType.Input), inputValue, inputConcentrationLevel);
		action.addSpeciesActions(inputSpeciesActions);

		if (speciesSet.hasSpecies(AcSpeciesType.Feedback) && feedbackConcentrationLevel != null) {
			final Collection<AcSpeciesInteraction> feedbackSpeciesActions = createBinarySpeciesActions(
					speciesSet.getSpecies(AcSpeciesType.Feedback), feedbackValue, feedbackConcentrationLevel);
			action.addSpeciesActions(feedbackSpeciesActions);
		}
		if (nullOutputSpecies) {
			for (AcSpecies outputSpecies : speciesSet.getSpecies(AcSpeciesType.Output)) {
				AcSpeciesInteraction outputSpeciesAction = new AcSpeciesInteraction();
				outputSpeciesAction.setSpecies(outputSpecies);
				outputSpeciesAction.setFunction(new Expression<Double, Double>("0"));
				action.addToSpeciesActions(outputSpeciesAction);
			}
		}
		return action;
	}

	private Collection<AcSpeciesInteraction> createBinarySpeciesActions(
		Collection<AcSpecies> species,
		Boolean value,
		AcConcentrationLevel concentrationLevel
	) {
		Collection<AcSpeciesInteraction> speciesActions = new ArrayList<AcSpeciesInteraction>();
		if (concentrationLevel.isSingleValue()) {
			speciesActions = createBinarySpeciesActions(
				species,
				value,
				concentrationLevel.getSingleValue());
		} else {
			speciesActions.add(createBinarySpeciesAction(
				ObjectUtil.getFirst(species),
				value,
				concentrationLevel.getLowValue(),
				concentrationLevel.getHighValue()));
		}
		return speciesActions;
	}

	private Collection<AcSpeciesInteraction> createBinarySpeciesActions(
		Collection<AcSpecies> species,
		Boolean value,
		Double concentration
	) {
		final int inputSpeciesNum = species.size();
		if (inputSpeciesNum != 2) {
			throw new BndChemistryException("For binary task (with two species per binary variable), overall two input species are expected, but got '" + inputSpeciesNum +"'.");
		}
		Collection<AcSpeciesInteraction> binarySpeciesActions = new ArrayList<AcSpeciesInteraction>();
		AcSpeciesInteraction value0SpeciesAction = new AcSpeciesInteraction();
		value0SpeciesAction.setSpecies(ObjectUtil.getFirst(species));
		binarySpeciesActions.add(value0SpeciesAction);

		AcSpeciesInteraction value1SpeciesAction = new AcSpeciesInteraction();
		value1SpeciesAction.setSpecies(ObjectUtil.getSecond(species));
		binarySpeciesActions.add(value1SpeciesAction);

		Function<Double, Double> concentrationConstFunction = new Expression<Double, Double>(concentration.toString());
		Function<Double, Double> concentrationZeroFunction = new Expression<Double, Double>("0");
		if (value) {
			value0SpeciesAction.setSettingFunction(concentrationConstFunction);
			value1SpeciesAction.setSettingFunction(concentrationZeroFunction);
		} else {
			value0SpeciesAction.setSettingFunction(concentrationZeroFunction);
			value1SpeciesAction.setSettingFunction(concentrationConstFunction);
		}
		return binarySpeciesActions;
	}

	private AcSpeciesInteraction createBinarySpeciesAction(
		AcSpecies species,
		Boolean value,
		Double value0Concetration,
		Double value1Concetration
	) {
		AcSpeciesInteraction binarySpeciesAction = new AcSpeciesInteraction();
		binarySpeciesAction.setSpecies(species);
		Double concentration = value0Concetration;
		if (value) {
			concentration = value1Concetration;
		}
		binarySpeciesAction.setSettingFunction(new Expression<Double, Double>(concentration.toString()));
		return binarySpeciesAction;
	}

	public Collection<Pair<Boolean, Boolean>> createInputOutputTemporalFunSeries(
		BooleanFunctionType booleanFunctionType,
		int seriesLength,
		int timeWindowLength
	) {
		Collection<Pair<Boolean, Boolean>> inputOutputPairs = new ArrayList<Pair<Boolean, Boolean>>();
		FunctionEvaluator<Boolean, Boolean> booleanFunctionEvaluator = 
				functionEvaluatorFactory.createInstance(new BooleanFunction(booleanFunctionType));
		List<Boolean> inputsInWindow = new ArrayList<Boolean>();
		for (int i = 0; i < seriesLength; i++) {
			if (inputsInWindow.size() == timeWindowLength) {
				inputsInWindow.remove(0);
			}
			final Boolean newInput = RandomUtil.nextBoolean();
			inputsInWindow.add(newInput);
			inputOutputPairs.add(new Pair<Boolean, Boolean>(newInput,
					booleanFunctionEvaluator.evaluate(inputsInWindow)));
		}
		return inputOutputPairs;
	}
}