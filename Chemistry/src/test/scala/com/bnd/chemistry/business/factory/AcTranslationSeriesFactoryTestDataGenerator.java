package com.bnd.chemistry.business.factory;

import java.util.ArrayList;
import java.util.Collection;

import com.bnd.chemistry.business.factory.AcInteractionSeriesFactoryTestDataGenerator.AcTemporalActionASWithInitData;
import com.bnd.chemistry.domain.AcInteractionSeries;
import com.bnd.chemistry.domain.AcConcentrationLevel;
import com.bnd.chemistry.domain.AcSpeciesType;
import com.bnd.function.business.ExpressionSupportedFunctionEvaluatorFactoryImpl;
import com.bnd.function.domain.AggregateFunction;
import com.bnd.function.enumerator.ListEnumeratorFactoryImpl;
import com.bnd.function.evaluator.FunctionEvaluatorFactory;


public class AcTranslationSeriesFactoryTestDataGenerator extends AcCoreTestDataGenerator {

	protected class AcSimplePeriodicInputOutputFeedbackTSData {
		protected AggregateFunction inputSpeciesStateTransFunction;
		protected AggregateFunction outputSpeciesStateTransFunction;
		protected AggregateFunction feedbackSpeciesStateTransFunction;
		protected AcConcentrationLevel inputConcentrationLevel;
		protected AcConcentrationLevel outputConcentrationLevel;
		protected AcConcentrationLevel feedbackConcentrationLevel;
		protected AcInteractionSeries quasiPeriodicActionSeries;
	}

	protected class AcSimplePeriodicFullTSData extends AcSimplePeriodicInputOutputFeedbackTSData {
		protected AggregateFunction internalSpeciesStateTransFunction;
	}

	private Collection<AcSimplePeriodicInputOutputFeedbackTSData> simplePeriodicInputOutputFeedbackTSData = new ArrayList<AcSimplePeriodicInputOutputFeedbackTSData>();
	private Collection<AcSimplePeriodicFullTSData> simplePeriodicFullTSData = new ArrayList<AcSimplePeriodicFullTSData>();
	private AcInteractionSeriesFactoryTestDataGenerator actionSeriesFactoryTestDataGenerator = new AcInteractionSeriesFactoryTestDataGenerator();
    private final FunctionEvaluatorFactory functionEvaluatorFactory = new ExpressionSupportedFunctionEvaluatorFactoryImpl(
    		new ListEnumeratorFactoryImpl());
	private AcInteractionSeriesFactory actionSeriesFactory = new AcInteractionSeriesFactory(functionEvaluatorFactory);

	public AcTranslationSeriesFactoryTestDataGenerator() {
		setUpTestData();
	}

	private void setUpTestData() {
		setSimplePeriodicInputOutputFeedbackTSData();
		setSimplePeriodicFullTSData();
	}

	private void setSimplePeriodicInputOutputFeedbackTSData() {
		for (AcTemporalActionASWithInitData asTestData : actionSeriesFactoryTestDataGenerator.getTemporalActionASWithInitData()) {
			AcInteractionSeries quasiPeriodicActionSeries = actionSeriesFactory.createTemporalActionASWithInit(
					asTestData.speciesSet,
					asTestData.functionType,
					asTestData.timeWindowLength,
					asTestData.initInternalConcDistribution,
					asTestData.initWeightConcDistribution,
					asTestData.inputConcentrationLevel,
					asTestData.feedbackConcentrationLevel,
					asTestData.actionsNum,
					asTestData.afterInitStartingTimeStep,
					asTestData.actionInterval,
					asTestData.nullOutputSpecies);
			AcSimplePeriodicInputOutputFeedbackTSData testData = new AcSimplePeriodicInputOutputFeedbackTSData();
			testData.inputSpeciesStateTransFunction = getRandomAggregateFunction();
			testData.outputSpeciesStateTransFunction = getRandomAggregateFunction();
			testData.feedbackSpeciesStateTransFunction = getRandomAggregateFunction();
			testData.inputConcentrationLevel = asTestData.inputConcentrationLevel;
			testData.outputConcentrationLevel = createConcentrationLevel(asTestData.speciesSet.getSpeciesNumber(AcSpeciesType.Output));
			testData.feedbackConcentrationLevel = asTestData.feedbackConcentrationLevel;
			testData.quasiPeriodicActionSeries = quasiPeriodicActionSeries;
			simplePeriodicInputOutputFeedbackTSData.add(testData);
		}
	}

	private void setSimplePeriodicFullTSData() {
		for (AcSimplePeriodicInputOutputFeedbackTSData simpleTestData : simplePeriodicInputOutputFeedbackTSData) {
			AcSimplePeriodicFullTSData testData = new AcSimplePeriodicFullTSData();
			testData.inputSpeciesStateTransFunction = simpleTestData.inputSpeciesStateTransFunction;
			testData.outputSpeciesStateTransFunction = simpleTestData.outputSpeciesStateTransFunction;
			testData.feedbackSpeciesStateTransFunction = simpleTestData.feedbackSpeciesStateTransFunction;
			testData.inputConcentrationLevel = simpleTestData.inputConcentrationLevel;
			testData.outputConcentrationLevel = simpleTestData.outputConcentrationLevel;
			testData.feedbackConcentrationLevel = simpleTestData.feedbackConcentrationLevel;
			testData.quasiPeriodicActionSeries = simpleTestData.quasiPeriodicActionSeries;
			testData.internalSpeciesStateTransFunction = randomElement(AggregateFunction.values());
			simplePeriodicFullTSData.add(testData);
		}
	}

	protected Collection<AcSimplePeriodicInputOutputFeedbackTSData> getSimplePeriodicInputOutputFeedbackTSData() {
		return simplePeriodicInputOutputFeedbackTSData;
	}

	protected Collection<AcSimplePeriodicFullTSData> getSimplePeriodicFullTSData() {
		return simplePeriodicFullTSData;
	}
}