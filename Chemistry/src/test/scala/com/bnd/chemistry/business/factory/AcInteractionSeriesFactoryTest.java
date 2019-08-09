package com.bnd.chemistry.business.factory;

import java.util.*;

import junit.framework.TestCase;

import org.junit.Test;

import com.bnd.chemistry.business.factory.AcInteractionSeriesFactoryTestDataGenerator.AcTemporalActionASWithInitData;
import com.bnd.chemistry.domain.*;
import com.bnd.core.util.ObjectUtil;
import com.bnd.function.business.ExpressionSupportedFunctionEvaluatorFactoryImpl;
import com.bnd.function.domain.Function;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import com.bnd.function.evaluator.FunctionEvaluator;
import com.bnd.function.evaluator.FunctionEvaluatorFactory;


public class AcInteractionSeriesFactoryTest extends TestCase {

	private final ListEnumeratorFactory listEnumeratorFactory = null; // new ListEnumeratorFactoryImpl()
    private final FunctionEvaluatorFactory functionEvaluatorFactory = new ExpressionSupportedFunctionEvaluatorFactoryImpl(listEnumeratorFactory);
	private AcInteractionSeriesFactoryTestDataGenerator testDataGenerator = new AcInteractionSeriesFactoryTestDataGenerator();
	private AcInteractionSeriesFactory actionSeriesFactory = new AcInteractionSeriesFactory(functionEvaluatorFactory);

	@Test
	public void testCreateTemporalActionASWithInit() {
		for (AcTemporalActionASWithInitData testData : testDataGenerator.getTemporalActionASWithInitData()) {
			final Collection<AcSpecies> internalSpecies = testData.speciesSet.getInternalSpecies();
			final Collection<AcSpecies> weightSpecies = testData.speciesSet.getSpecies(AcSpeciesType.Functional);
			final Collection<AcSpecies> inputSpecies = testData.speciesSet.getSpecies(AcSpeciesType.Input);
			final Collection<AcSpecies> feedbackSpecies = testData.speciesSet.getSpecies(AcSpeciesType.Feedback);
			final Collection<AcSpecies> outputSpecies = testData.speciesSet.getSpecies(AcSpeciesType.Output);
			final int inputSpeciesNum = inputSpecies.size();
			final int feedbackSpeciesNum = feedbackSpecies.size();
			checkSpeciesNum(testData.inputConcentrationLevel, inputSpeciesNum);
			checkSpeciesNum(testData.feedbackConcentrationLevel, feedbackSpeciesNum);

			AcInteractionSeries actionSeries = actionSeriesFactory.createTemporalActionASWithInit(
					testData.speciesSet,
					testData.functionType,
					testData.timeWindowLength,
					testData.initInternalConcDistribution,
					testData.initWeightConcDistribution,
					testData.inputConcentrationLevel,
					testData.feedbackConcentrationLevel,
					testData.actionsNum,
					testData.afterInitStartingTimeStep,
					testData.actionInterval,
					testData.nullOutputSpecies);

			assertNotNull(actionSeries);
			assertNotNull(actionSeries.getSpeciesSet());
			assertEquals(testData.actionsNum + 1, actionSeries.getActions().size());

			List<AcInteraction> sortedActions = new ArrayList<AcInteraction>();
			sortedActions.addAll(actionSeries.getActions());
			Collections.sort(sortedActions);
			Iterator<AcInteraction> actionInterator = sortedActions.iterator();
			AcInteraction initAction = actionInterator.next();
			for (AcSpeciesInteraction speciesAction : initAction.getSpeciesActions()) {
				final AcSpecies species = speciesAction.getSpecies();
				assertTrue(internalSpecies.contains(species) || weightSpecies.contains(species));
			}
			int previousStep = 0;
			while (actionInterator.hasNext()) {
				final AcInteraction trainingAction = actionInterator.next();
				if (previousStep == 0) {
					assertEquals(testData.afterInitStartingTimeStep, trainingAction.getStartTime().intValue());
				} else {
					assertEquals(testData.actionInterval, trainingAction.getStartTime() - previousStep);
				}
				previousStep = trainingAction.getStartTime();
				checkTraningAction(
					trainingAction,
					inputSpecies,
					feedbackSpecies,
					outputSpecies,
					testData.inputConcentrationLevel,
					testData.feedbackConcentrationLevel,
					testData.nullOutputSpecies);
			}
		}
	}

	private void checkSpeciesNum(AcConcentrationLevel concentrationLevel, int domainSpeciesNum) {
		if (concentrationLevel == null) {
			return;
		}
		if (domainSpeciesNum == 1) {
			assertTrue(!concentrationLevel.isSingleValue());
		} else if (domainSpeciesNum == 2) {
			assertTrue(concentrationLevel.isSingleValue());				
		} else {
			assertTrue("The number of domain species can be either 1 or 2, but got '" + domainSpeciesNum + "'.", false);
		}
	}

	private void checkSpeciesConcentration(AcConcentrationLevel concentrationLevel, Double speciesConcentration) {
		if (concentrationLevel.isSingleValue()) {
			if (!ObjectUtil.areObjectsEqual(concentrationLevel.getSingleValue(), speciesConcentration)) {
				assertEquals(0d, speciesConcentration);
			}
		} else {
			if (!ObjectUtil.areObjectsEqual(concentrationLevel.getLowValue(), speciesConcentration)) {
				assertEquals(concentrationLevel.getHighValue(), speciesConcentration);	
			}
		}
	}

	private void checkTraningAction(
		AcInteraction trainingAction,
		Collection<AcSpecies> inputSpecies,
		Collection<AcSpecies> feedbackSpecies,
		Collection<AcSpecies> outputSpecies,
		AcConcentrationLevel inputConcentrationLevel,
		AcConcentrationLevel feedbackConcentrationLevel,
		boolean includeOutputSpecies
	) {
		Collection<AcSpecies> inputSpeciesCopy = new ArrayList<AcSpecies>();
		inputSpeciesCopy.addAll(inputSpecies);
		Collection<AcSpecies> feedbackSpeciesCopy = new ArrayList<AcSpecies>();
		feedbackSpeciesCopy.addAll(feedbackSpecies);
		Collection<AcSpecies> outputSpeciesCopy = new ArrayList<AcSpecies>();
		if (includeOutputSpecies) {
			outputSpeciesCopy.addAll(outputSpecies);
		}
		for (AcSpeciesInteraction speciesAction : trainingAction.getSpeciesActions()) {
			AcSpecies species = speciesAction.getSpecies();
			Double speciesConcentration = evaluateConstFunction(speciesAction.getSettingFunction());
			if (inputSpeciesCopy.remove(species)) {
				checkSpeciesConcentration(inputConcentrationLevel, speciesConcentration);
			} else if (feedbackSpeciesCopy.remove(species)) {
				checkSpeciesConcentration(feedbackConcentrationLevel, speciesConcentration);
			} else if (outputSpeciesCopy.remove(species)) {
				checkSpeciesConcentration(new AcConcentrationLevel(0d), speciesConcentration);
			} else {
				assertTrue("Species " + species.getLabel() + " is neither input nor feedback species.", false);
			}
		}
		assertTrue(inputSpeciesCopy.isEmpty());
		assertTrue(feedbackSpeciesCopy.isEmpty());
		assertTrue(outputSpeciesCopy.isEmpty());			
	}

	private Double evaluateConstFunction(Function<Double, Double> function) {
		FunctionEvaluator<Double, Double> funEvaluator = functionEvaluatorFactory.createInstance(function);
		return funEvaluator.evaluate(new Double[0]);
	}
}