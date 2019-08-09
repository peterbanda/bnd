package com.bnd.chemistry.business.factory;

import java.util.ArrayList;
import java.util.Collection;

import com.bnd.chemistry.domain.AcConcentrationLevel;
import com.bnd.chemistry.domain.AcSpeciesSet;
import com.bnd.chemistry.domain.AcSpeciesType;
import com.bnd.function.domain.BooleanFunction.BooleanFunctionType;
import com.bnd.math.domain.rand.RandomDistribution;


public class AcInteractionSeriesFactoryTestDataGenerator extends AcCoreTestDataGenerator {

	protected class AcTemporalActionASWithInitData {
		protected AcSpeciesSet speciesSet;
		protected BooleanFunctionType functionType;
		protected int timeWindowLength;
		protected RandomDistribution<Double> initInternalConcDistribution;
		protected RandomDistribution<Double> initWeightConcDistribution;
		protected AcConcentrationLevel inputConcentrationLevel;
		protected AcConcentrationLevel feedbackConcentrationLevel;
		protected int actionsNum;
		protected int afterInitStartingTimeStep;
		protected int actionInterval;
		protected boolean nullOutputSpecies;
	}

	private Collection<AcTemporalActionASWithInitData> temporalActionASWithInitData = new ArrayList<AcTemporalActionASWithInitData>();

	public AcInteractionSeriesFactoryTestDataGenerator() {
		setUpTestData();
	}

	private void setUpTestData() {
		setTemporalActionASWithInitData();
	}

	private void setTemporalActionASWithInitData() {
		for (int i = 0; i < 100; i++) {
			AcTemporalActionASWithInitData testData = new AcTemporalActionASWithInitData();
			testData.speciesSet = createSpeciesSetWithGroups(randomBoolean());
			testData.functionType = randomElement(BooleanFunctionType.values());
			testData.timeWindowLength = randomInt(1, 10);
			testData.initInternalConcDistribution = createRandomDistribution();
			testData.inputConcentrationLevel = createConcentrationLevel(testData.speciesSet.getSpeciesNumber(AcSpeciesType.Input));
			if (testData.speciesSet.hasSpecies(AcSpeciesType.Feedback)) {
				testData.initWeightConcDistribution = createRandomDistribution();
				testData.feedbackConcentrationLevel = createConcentrationLevel(testData.speciesSet.getSpeciesNumber(AcSpeciesType.Feedback));
			}
			testData.actionsNum = randomInt(10, 100);
			testData.afterInitStartingTimeStep = randomInt(1, 100);
			testData.actionInterval = randomInt(500, 5000);
			testData.nullOutputSpecies = randomBoolean();
			temporalActionASWithInitData.add(testData);
		}
	}

	protected Collection<AcTemporalActionASWithInitData> getTemporalActionASWithInitData() {
		return temporalActionASWithInitData;
	}
}